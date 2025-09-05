package com.coding24h.mall_spring.service;

import com.coding24h.mall_spring.dto.*;
import com.coding24h.mall_spring.entity.Category;
import com.coding24h.mall_spring.entity.ContentModeration;
import com.coding24h.mall_spring.entity.Product;
import com.coding24h.mall_spring.entity.ProductImage;
import com.coding24h.mall_spring.entity.vo.PageResult;
import com.coding24h.mall_spring.entity.vo.ProductDetailVO;
import com.coding24h.mall_spring.entity.vo.ProductVO;
import com.coding24h.mall_spring.mapper.*;
import com.coding24h.mall_spring.mapper.recommend.RecommendMapper;
import com.coding24h.mall_spring.service.impl.FileStorageService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService{

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ContentModerationMapper contentModerationMapper;
    @Autowired
    private ProductImageMapper productImageMapper;

    @Autowired
    private ProductFavoriteMapper productFavoriteMapper;

    @Autowired
    private RecommendMapper recMapper;


    @Transactional
    public Integer submitProductForReview(ProductSubmitDTO dto, Integer sellerId) {
        // 1. 处理图片上传
        List<String> imageUrls = new ArrayList<>();
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            for (MultipartFile image : dto.getImages()) {
                if (image != null && !image.isEmpty()) {
                    String imageUrl = fileStorageService.storeFile(image, "products");
                    imageUrls.add(imageUrl);
                }
            }
        }

        // 2. 保存商品基本信息
        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        product.setSellerId(sellerId);
        product.setStatus(4); // 审核中
        productMapper.insertProduct(product);

        // 3. 保存图片信息（使用主图索引）
        List<ProductImage> images = new ArrayList<>();
        for (int i = 0; i < imageUrls.size(); i++) {
            boolean isMain = (i == dto.getMainImageIndex());
            images.add(new ProductImage(
                    product.getProductId(),
                    imageUrls.get(i),
                    isMain
            ));
        }

        if (!images.isEmpty()) {
            productMapper.batchInsertImages(images);
        }

        // 4. 创建审核记录（保持不变）
        ContentModeration moderation = new ContentModeration();
        moderation.setContentId(product.getProductId());
        moderation.setContent(product.getTitle() + "\n" + product.getDescription());
        contentModerationMapper.insertModeration(moderation);

        return product.getProductId();
    }

    public ProductSearchResponse searchProducts(ProductQueryDTO query, Long currentUserId) {
        // 设置分页参数
        int page = query.getPage() != null && query.getPage() > 0 ? query.getPage() : 1;
        int pageSize = query.getPageSize() != null && query.getPageSize() > 0 ? query.getPageSize() : 12;
        int offset = (page - 1) * pageSize;

        // 执行查询
        List<ProductVO> items = productMapper.searchProducts(query, currentUserId, offset, pageSize);
        long total = productMapper.countProducts(query, currentUserId);

        return new ProductSearchResponse(total, items);
    }

    // 新增：获取推荐商品
    public ProductSearchResponse getRecommendedProducts(ProductQueryDTO query, Long currentUserId) {
        // 设置分页参数
        int page = query.getPage() != null && query.getPage() > 0 ? query.getPage() : 1;
        int pageSize = query.getPageSize() != null && query.getPageSize() > 0 ? query.getPageSize() : 12;
        int offset = (page - 1) * pageSize;

        // 1. 获取用户感兴趣的商品分类ID列表
        List<Integer> interestedCategoryIds = recMapper.findUserInterestedCategories(currentUserId);  //猜你喜欢
        if (interestedCategoryIds.isEmpty()) {
            // 如果用户没有明确的兴趣，可以返回热门商品或最新商品作为兜底
            query.setSort("popular");
            return searchProducts(query, currentUserId);
        }

        // 2. 查询推荐商品
        List<ProductVO> items = recMapper.findRecommendedProducts(currentUserId, interestedCategoryIds, offset, pageSize);

        // 3. 查询推荐商品总数
        long total = recMapper.countRecommendedProducts(currentUserId, interestedCategoryIds);

        return new ProductSearchResponse(total, items);
    }


    /**
     * "看了又看" (相似商品推荐)
     * 优先使用基于协同过滤的 "共同购买" 推荐。
     * 如果协同过滤结果为空，则降级为基于内容的 "同分类热门" 推荐。
     *
     * @param productId     当前商品ID
     * @param currentUserId 当前用户ID（用于排除用户自己的商品或已购买的）
     * @param page          页码
     * @param size          每页数量
     * @return 推荐商品的分页结果
     */
    public ProductSearchResponse findSimilarProducts(Integer productId, Long currentUserId, int page, int size) {
        int offset = (page - 1) * size;

        // --- 进阶版：基于协同过滤 (Item-Based Collaborative Filtering) ---
        // 查找与 productId 一起被购买过的其他商品
        List<ProductVO> collaborativeItems = recMapper.findProductsFrequentlyBoughtTogether(productId, currentUserId, size);

        // 如果协同过滤找到了足够的结果，直接返回
        if (!collaborativeItems.isEmpty()) {
            // 注意：这里的 total 只是当前批次的结果数，因为协同过滤通常不进行严格分页
            long total = collaborativeItems.size();
            return new ProductSearchResponse(total, collaborativeItems);
        }

        // --- 简单版：基于内容推荐 (作为降级策略) ---
        // 1. 获取当前商品的分类ID
        Product currentProduct = productMapper.selectById(productId);
        if (currentProduct == null) {
            // 如果商品不存在，返回空结果
            return new ProductSearchResponse(0L, Collections.emptyList());
        }
        Integer categoryId = currentProduct.getCategoryId();

        // 2. 查找该分类下的其他热门商品 (按浏览量和创建时间排序)
        List<ProductVO> contentBasedItems = recMapper.findSimilarProductsByCategory(categoryId, productId, currentUserId, offset, size);
        long total = recMapper.countSimilarProductsByCategory(categoryId, productId, currentUserId);

        return new ProductSearchResponse(total, contentBasedItems);
    }

    /**
     * 新增：获取购物车配套推荐商品
     * 核心思路：找出与购物车内商品最常被一起购买的其他商品。
     * @param query         分页和排序参数
     * @param currentUserId 当前用户ID
     * @return 推荐商品的分页结果
     */
    public ProductSearchResponse getCartRecommendations(ProductQueryDTO query, Long currentUserId) {
        // 1. 从 recMapper 获取当前用户购物车中的所有商品ID
        List<Integer> cartProductIds = recMapper.findProductIdsInCart(currentUserId);

        // 如果购物车为空，则无需推荐，直接返回空列表
        if (cartProductIds == null || cartProductIds.isEmpty()) {
            return new ProductSearchResponse(0L, Collections.emptyList());
        }

        // 2. 设置分页参数 (购物车推荐通常数量较少)
        int page = query.getPage() != null && query.getPage() > 0 ? query.getPage() : 1;
        int pageSize = query.getPageSize() != null && query.getPageSize() > 0 ? query.getPageSize() : 6;
        int offset = (page - 1) * pageSize;

        // 3. 调用 recMapper 查询与购物车商品搭配的推荐品
        List<ProductVO> items = recMapper.findComplementaryProductsForCart(currentUserId, cartProductIds, offset, pageSize);

        // 4. 调用 recMapper 计算推荐品的总数，用于分页
        long total = recMapper.countComplementaryProductsForCart(currentUserId, cartProductIds);

        return new ProductSearchResponse(total, items);
    }

    /**
     * 获取所有激活状态的分类列表
     * 该方法从数据库中查询并返回所有状态为激活的分类信息
     *
     * @return List<Category> 包含所有激活分类的列表，如果不存在任何激活分类则返回空列表
     */
    public List<Category> getAllActiveCategories() {
        // 调用数据访问层的方法获取所有激活状态的分类
        return productMapper.findAllActiveCategories();
    }


    /**
     * 获取所有分类，并构建成树形结构
     */
    public List<Category> getAllCategoriesAsTree() {
        // 注意：这里改为获取所有分类，以便在管理界面看到被隐藏的分类
        List<Category> allCategories = productMapper.findAllCategoriesTree();

        Map<Integer, Category> categoryMap = allCategories.stream()
                .collect(Collectors.toMap(Category::getCategoryId, category -> category));

        return allCategories.stream().filter(category -> {
            Integer parentId = category.getParentId();
            if (parentId == null || parentId == 0 || !categoryMap.containsKey(parentId)) {
                return true;
            }
            Category parent = categoryMap.get(parentId);
            if (parent.getChildren() == null) {
                parent.setChildren(new java.util.ArrayList<>());
            }
            parent.getChildren().add(category);
            return false;
        }).collect(Collectors.toList());
    }

    /**
     * 新增：创建一个新的分类
     * @param category 要创建的分类对象
     * @return 创建后的分类对象（包含ID）
     */
    public Category createCategory(Category category) {
        // 如果 parentId 为 null，则设置为0，表示顶级分类
        if (category.getParentId() == null) {
            category.setParentId(0);
        }
        productMapper.insertCategory(category);
        return category;
    }

    /**
     * 新增：更新一个分类
     * @param category 要更新的分类对象
     * @return 更新后的行数
     */
    public int updateCategory(Category category) {
        if (category.getParentId() == null) {
            category.setParentId(0);
        }
        return productMapper.updateCategory(category);
    }

    /**
     * 新增：删除一个分类及其所有子分类
     * @param categoryId 要删除的分类ID
     */
    @Transactional // 添加事务支持，确保操作的原子性
    public void deleteCategoryAndChildren(Integer categoryId) {
        // 1. 查找当前分类下的所有直接子分类
        List<Category> children = productMapper.findCategoriesByParentId(categoryId);

        // 2. 递归删除所有子分类
        for (Category child : children) {
            deleteCategoryAndChildren(child.getCategoryId());
        }

        // 3. 删除当前分类
        productMapper.deleteCategoryById(categoryId);
    }


    @Transactional
    public PageResult<FavoriteProductDTO> listUserFavorites(Long userId, Integer page, Integer size, String sort) {
        PageHelper.startPage(page, size);
        List<FavoriteProductDTO> items = productFavoriteMapper.listUserFavorites(userId, sort);

        PageInfo<FavoriteProductDTO> pageInfo = new PageInfo<>(items);
        long total = pageInfo.getTotal();
        return new PageResult<>(total, items);
    }


    @Transactional
    public void toggleFavorite(Long userId, Integer productId) {
        int isFavorited = productFavoriteMapper.isFavorited(userId, productId);
        if (isFavorited > 0) {
            // 已收藏，则取消收藏
            productFavoriteMapper.removeFavorite(userId, productId);
            productMapper.updateFavoriteCount(productId, -1);
        } else {
            // 未收藏，则添加收藏
            productFavoriteMapper.addFavorite(userId, productId);
            productMapper.updateFavoriteCount(productId, 1);
        }
    }


    @Transactional
    public Integer createProduct(Integer sellerId, ProductFormDTO form) {
        Product product = new Product();
        product.setSellerId(sellerId);
        product.setTitle(form.getTitle());
        product.setDescription(form.getDescription());
        product.setPrice(form.getPrice());
        product.setOriginalPrice(form.getOriginalPrice());
        product.setStock(form.getStock());
        product.setCategoryId(form.getCategoryId());
        product.setLocation(form.getLocation());
        product.setCondition(form.getCondition());
        // product.setCustomAttributes(form.getSpecs());
        product.setStatus(4); // 设置商品状态为有效

        productMapper.insertProduct(product);
        return product.getProductId(); // 返回新建商品ID
    }



    @Transactional
    public Integer updateProduct(Integer productId, ProductSubmitDTO dto, Integer sellerId) {
        // 1. 验证商品是否存在且属于当前卖家
        Product existingProduct = productMapper.selectById(productId);
        if (existingProduct == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!existingProduct.getSellerId().equals(sellerId)) {
            throw new RuntimeException("无权修改此商品");
        }

        // 2. 如果有新图片上传，先处理图片
        boolean hasNewImages = dto.getImages() != null && !dto.getImages().isEmpty();
        List<String> newImageUrls = new ArrayList<>();

        if (hasNewImages) {
            for (MultipartFile image : dto.getImages()) {
                if (image != null && !image.isEmpty()) {
                    String imageUrl = fileStorageService.storeFile(image, "products");
                    newImageUrls.add(imageUrl);
                }
            }
        }

        // 3. 如果有新图片，需要删除旧的物理文件和数据库记录
        if (hasNewImages) {
            // 获取旧图片URL用于删除物理文件
            List<String> oldImageUrls = productImageMapper.selectImageUrlsByProductId(productId);

            // 删除旧的物理文件
            for (String oldImageUrl : oldImageUrls) {
                try {
                    fileStorageService.deleteFileByUrl(oldImageUrl);
                } catch (Exception e) {
                    // 记录错误但不中断流程
                    System.out.println("删除旧图片失败: " + oldImageUrl + ", 错误信息: " + e.getMessage());
                }
            }

            // 删除旧的数据库图片记录
            productImageMapper.deleteByProductId(productId);
        }

        // 4. 更新商品基本信息
        BeanUtils.copyProperties(dto, existingProduct);
        existingProduct.setProductId(productId); // 确保ID不变
        existingProduct.setSellerId(sellerId);
        existingProduct.setUpdatedAt(new Date());
        existingProduct.setStatus(4); // 审核中

        productMapper.updateProduct(existingProduct);

        // 5. 如果有新图片，保存新图片信息
        if (hasNewImages && !newImageUrls.isEmpty()) {
            List<ProductImage> images = new ArrayList<>();
            for (int i = 0; i < newImageUrls.size(); i++) {
                boolean isMain = (i == dto.getMainImageIndex());
                images.add(new ProductImage(
                        productId,
                        newImageUrls.get(i),
                        isMain
                ));
            }
            productMapper.batchInsertImages(images);
        }

        // 6. 更新审核记录
        ContentModeration moderation = contentModerationMapper.getModerationByContentId(productId);
        if (moderation != null) {
            moderation.setContent(existingProduct.getTitle() + "\n" + existingProduct.getDescription());
            moderation.setStatus(0); // 重新设置为待审核
            moderation.setUpdatedAt(LocalDateTime.now());
            contentModerationMapper.updateModeration(moderation);
        } else {
            // 创建新的审核记录
            moderation = new ContentModeration();
            moderation.setContentId(productId);
            moderation.setContent(existingProduct.getTitle() + "\n" + existingProduct.getDescription());
            contentModerationMapper.insertModeration(moderation);
        }

        return productId;
    }

    @Transactional
    public void deleteProduct(Long sellerId, Integer productId) {
        // 1. 验证商品所有权
        Product product = productMapper.selectById(productId);
        if (product == null || !product.getSellerId().equals(sellerId.intValue())) {
            throw new IllegalArgumentException("无权操作此商品");
        }

        // 2. 获取商品的所有图片URL
        List<String> imageUrls = productImageMapper.selectImageUrlsByProductId(productId);

        // 3. 删除物理图片文件
        for (String imageUrl : imageUrls) {
            try {
                // 添加调试信息
                System.out.println("准备删除图片: " + imageUrl);
                fileStorageService.deleteFileByUrl(imageUrl);
            } catch (Exception e) {
                // 记录错误但不中断删除流程
                System.out.println("删除图片失败: " + imageUrl + ", 错误信息: " + e.getMessage());
            }
        }

        // 4. 删除数据库记录（先图片后商品）
        productImageMapper.deleteByProductId(productId);
        productMapper.deleteById(productId);
    }



    public ApiResponse<PageResult<ProductForSellerDTO>> getSellerProducts(Long sellerId, int page, int size) {
        // 计算分页偏移量
        int offset = (page - 1) * size;

        // 1. 查询商品列表
        List<ProductForSellerDTO> products = productMapper.selectSellerProducts(
                sellerId,
                offset,
                size
        );

        // 2. 查询商品总数
        int total = productMapper.countSellerProducts(sellerId);

        // 3. 封装分页结果
        PageResult<ProductForSellerDTO> pageResult = new PageResult<>();
        pageResult.setTotal(total);
        pageResult.setItems(products);

        return ApiResponse.success(pageResult);
    }


    public ProductDetailVO getProductDetailById(Integer productId, Long userId) {
        // 1. 获取商品详情（包含分类和图片）
        ProductDetailVO detailVO = productMapper.getProductDetailById(productId);

        if (detailVO != null) {
            // 2. 检查用户是否收藏
            if (userId != null) {
                int count = productFavoriteMapper.isFavorited(userId, productId);
                detailVO.setFavoritedByCurrentUser(count > 0);
            }
            // 3. 更新浏览量
            productMapper.incrementViewCount(productId);
        }
        return detailVO;
    }

    public ApiResponse<ProductDetailDTO> getProductDetail(Integer productId, Integer userId) {
        // 先获取商品详情
        ProductDetailDTO productDetail = productMapper.selectProductDetailById(productId, userId);

        if (productDetail == null) {
            System.out.println("商品不存在");
        }

        // 更新浏览量（复用现有方法）
        productMapper.incrementViewCount(productId);

        // 获取收藏量（如果前端需要）
        // productDetail.setFavoriteCount(productMapper.getFavoriteCount(productId));

        return ApiResponse.success(productDetail);
    }

    public String getMainImageByProductId(Integer productId) {
        if (productId == null) {
            return null;
        }
        return productMapper.selectMainImageByProductId(productId);
    }
}
