package com.coding24h.mall_spring.service;

import com.coding24h.mall_spring.dto.ShopSettingsUpdateDTO;
import com.coding24h.mall_spring.entity.Seller;
import com.coding24h.mall_spring.entity.vo.SellerReviewVO;
import com.coding24h.mall_spring.entity.vo.SellerShopVO;
import com.coding24h.mall_spring.entity.vo.ShopStatsVO;
import com.coding24h.mall_spring.mapper.OrderMapper;
import com.coding24h.mall_spring.mapper.ProductMapper;
import com.coding24h.mall_spring.mapper.SellerMapper;
import com.coding24h.mall_spring.mapper.review.ReviewMapper;
import com.coding24h.mall_spring.service.impl.FileStorageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Service
public class ShopService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private SellerMapper sellerMapper;

    public ShopStatsVO getShopStats(Long sellerId) {
        ShopStatsVO stats = new ShopStatsVO();

        // 在售商品数量
        stats.setOnSaleCount(productMapper.countOnSaleProducts(sellerId));

        // 本月订单数量
        stats.setMonthlyOrders(orderMapper.countMonthlyOrders(sellerId));

        // 店铺评分
        stats.setRating(reviewMapper.getAverageRating(sellerId));

        // 本月浏览量
        stats.setMonthlyViews(productMapper.sumMonthlyViews(sellerId));

        return stats;
    }

    // 平台固定政策
    private static final String PLATFORM_LOGISTICS_POLICY = "平台统一物流政策：满99元包邮，默认使用平台合作快递，急速发货。";
    private static final String PLATFORM_AFTER_SALE_POLICY = "平台统一售后政策：支持7天无理由退换货，品质问题商家承担运费。";
    private static final String PLATFORM_GUARANTEE_POLICY = "平台统一保障服务：正品保障，假一赔三，售后无忧。";

    /**
     * 获取店铺统计数据
     * @param sellerId 卖家ID
     * @return ShopStatsVO
     */
    public SellerShopVO getSellerShopStats(Long sellerId) {
        // 1. 从 sellers 表获取基础统计信息
        SellerShopVO stats = sellerMapper.getSellerStats(sellerId);
        if (stats == null) {
            // 如果卖家信息不存在，可以抛出异常或返回一个空的VO
            throw new RuntimeException("未找到卖家信息");
        }

        // 2. 获取在售商品数量
        long onSaleCount = sellerMapper.getOnSaleProductCount(sellerId);
        stats.setOnSaleCount(onSaleCount);

        // 3. 获取待处理订单数
        long pendingOrders = sellerMapper.getPendingOrderCount(sellerId);
        stats.setPendingOrders(pendingOrders);

        // 4. 设置固定的平台政策
        stats.setLogisticsPolicy(PLATFORM_LOGISTICS_POLICY);
        stats.setAfterSalePolicy(PLATFORM_AFTER_SALE_POLICY);
        stats.setGuaranteePolicy(PLATFORM_GUARANTEE_POLICY);

        return stats;
    }

    /**
     * 获取指定卖家的所有商品评价
     * @param sellerId 卖家ID
     * @return 评价列表
     */
    public List<SellerReviewVO> getReviewsForSeller(Long sellerId) {
        List<SellerReviewVO> reviews = reviewMapper.findReviewsBySellerId(sellerId);

        // 处理图片URL字符串，将其转换为列表
        reviews.forEach(review -> {
            Object urlsObject = review.getImageUrls(); // Mapper returns it as a list due to type handler, but let's be safe
            if (urlsObject instanceof String) {
                String imageUrlsString = (String) urlsObject;
                if (imageUrlsString != null && !imageUrlsString.isEmpty()) {
                    review.setImageUrls(Arrays.asList(imageUrlsString.split(",")));
                }
            }
        });

        return reviews;
    }

    /**
     * 更新店铺设置
     */
    public boolean updateShopSettings(Long sellerId, ShopSettingsUpdateDTO dto) {
        Seller seller = new Seller();
        Integer userId = sellerId.intValue();
        seller.setSellerId(userId);

        // 复制属性
        BeanUtils.copyProperties(dto, seller);

        // 特殊处理字段名不一致的情况
        seller.setShopName(dto.getShopName());

        int result = sellerMapper.updateShopSettings(seller);
        return result > 0;
    }

    /**
     * 上传店铺Logo
     */
    public String uploadShopLogo(Long sellerId, MultipartFile file) {
        // 获取当前店铺信息，包括旧Logo URL
        Seller currentSeller = getShopInfo(sellerId);
        String oldLogoUrl = currentSeller.getLogoUrl();

        // 删除旧Logo（如果存在）
        if (oldLogoUrl != null && !oldLogoUrl.isEmpty()) {
            try {
                fileStorageService.deleteFileByUrl(oldLogoUrl);
            } catch (Exception e) {
                // 记录日志但继续执行上传操作
                System.err.println("删除旧Logo失败: " + e.getMessage());
            }
        }

        // 上传新Logo
        String fileUrl = fileStorageService.storeFile(file, "shop_logos");

        // 更新数据库中的logo_url
        Seller seller = new Seller();
        Integer userId = sellerId.intValue();
        seller.setSellerId(userId);
        seller.setLogoUrl(fileUrl);
        sellerMapper.updateShopSettings(seller);

        return fileUrl;
    }

    /**
     * 上传店铺Banner
     */
    public String uploadShopBanner(Long sellerId, MultipartFile file) {
        // 获取当前店铺信息，包括旧Banner URL
        Seller currentSeller = getShopInfo(sellerId);
        String oldBannerUrl = currentSeller.getBannerUrl();

        // 删除旧Banner（如果存在）
        if (oldBannerUrl != null && !oldBannerUrl.isEmpty()) {
            try {
                fileStorageService.deleteFileByUrl(oldBannerUrl);
            } catch (Exception e) {
                // 记录日志但继续执行上传操作
                System.err.println("删除旧Banner失败: " + e.getMessage());
            }
        }

        // 上传新Banner
        String fileUrl = fileStorageService.storeFile(file, "shop_banners");

        // 更新数据库中的banner_url
        Seller seller = new Seller();
        Integer userId = sellerId.intValue();
        seller.setSellerId(userId);
        seller.setBannerUrl(fileUrl);
        sellerMapper.updateShopSettings(seller);

        return fileUrl;
    }

    /**
     * 获取店铺信息
     */
    public Seller getShopInfo(Long sellerId) {
        Integer userId = sellerId.intValue();
        return sellerMapper.selectBySellerId(userId);
    }
}
