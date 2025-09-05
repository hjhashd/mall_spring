package com.coding24h.mall_spring.service;

import com.coding24h.mall_spring.dto.order.AfterSaleRequestDTO;
import com.coding24h.mall_spring.dto.order.OrderDTO;
import com.coding24h.mall_spring.dto.order.OrderItemDTO;
import com.coding24h.mall_spring.dto.order.OrderListDTO;
import com.coding24h.mall_spring.entity.*;
import com.coding24h.mall_spring.entity.event.OrderCreatedEvent;
import com.coding24h.mall_spring.entity.event.OrderShippedEvent;
import com.coding24h.mall_spring.mapper.*;
import com.coding24h.mall_spring.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private ShippingCompanyMapper shippingCompanyMapper;

    // 新增：注入 SellerMapper
    @Autowired
    private SellerMapper sellerMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher; // 注入事件发布器
    /**
     * 【新增】为售后页面获取订单项详情
     */
    public OrderItemDTO getOrderItemDetailForUser(Integer userId, Integer orderItemId) {
        OrderItem orderItem = orderItemMapper.selectById(orderItemId);
        if (orderItem == null) {
            throw new BusinessException("订单项不存在");
        }

        Order order = orderMapper.selectByOrderId(orderItem.getOrderId());
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("无权访问该订单项");
        }

        // 使用你已有的转换方法
        return convertToOrderItemDTOs(List.of(orderItem)).get(0);
    }

    @Transactional
    public List<OrderDTO> processMultiSellerCheckout(
            Integer userId,
            String paymentMethod,
            Integer addressId,
            String remark,
            String shippingCompany) {

        // 1. 获取购物车商品
        List<Cart> cartItems = cartMapper.selectByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new BusinessException("购物车为空");
        }

        // 2. 获取收货地址信息
        UserAddress userAddress = userAddressMapper.selectById(addressId);
        if (userAddress == null || !userAddress.getUserId().equals(userId)) {
            throw new BusinessException("收货地址不存在或无权限访问");
        }

        // 3. 按卖家分组购物车商品
        Map<Integer, List<Cart>> sellerCartMap = groupCartItemsBySeller(cartItems);

        // 4. 验证库存
        validateInventory(sellerCartMap);

        // 5. 处理支付
        if ("balance".equals(paymentMethod)) {
            BigDecimal totalAmount = calculateTotalAmount(cartItems);
            handleBalancePayment(userId, totalAmount);
        }

        // 6. 为每个卖家创建订单
        List<OrderDTO> createdOrders = new ArrayList<>();
        for (Map.Entry<Integer, List<Cart>> entry : sellerCartMap.entrySet()) {
            Integer sellerId = entry.getKey();
            List<Cart> sellerCartItems = entry.getValue();

            OrderDTO order = createOrderForSeller(
                    userId, sellerId, sellerCartItems, paymentMethod,
                    userAddress, remark, shippingCompany
            );
            createdOrders.add(order);
        }

        // 7. 清空购物车
        cartMapper.deleteByUserId(userId);

        return createdOrders;
    }

    /**
     * 为特定卖家创建订单
     */
    private OrderDTO createOrderForSeller(
            Integer userId,
            Integer sellerId,
            List<Cart> sellerCartItems,
            String paymentMethod,
            UserAddress userAddress,
            String remark,
            String shippingCompany) {

        // 1. 计算该卖家的订单总金额
        BigDecimal sellerTotalAmount = calculateSellerTotalAmount(sellerCartItems);

        // 2. 创建订单
        Order order = new Order();
        order.setOrderId(generateOrderId());
        order.setUserId(userId);
        order.setSellerId(sellerId);
        order.setTotalAmount(sellerTotalAmount);
        order.setStatus(2); // 2-待发货（已支付）
        order.setPaymentMethod(paymentMethod);
        order.setShippingAddressId(userAddress.getAddressId());

        // 冗余存储收货信息
        order.setReceiverName(userAddress.getReceiverName());
        order.setReceiverPhone(userAddress.getReceiverPhone());
        order.setShippingAddress(formatFullAddress(userAddress));
        order.setShippingCompany(shippingCompany);
        order.setLogisticsStatus("待发货");

        order.setCreatedAt(LocalDateTime.now());
        order.setPaidAt(LocalDateTime.now());
        order.setUserRemark(remark);

        // 3. 插入订单
        orderMapper.insertOrder(order);

        // 4. 创建订单项
        createOrderItems(order.getOrderId(), sellerCartItems);

        // 5. 创建支付记录
        createPaymentRecord(order.getOrderId(), sellerTotalAmount, paymentMethod);

        // 6. 更新库存、卖家余额和卖家总销量
        updateInventoryAndSellerStats(sellerCartItems, sellerId);
        // 【核心新增】发布订单创建事件，用于通知卖家
        eventPublisher.publishEvent(new OrderCreatedEvent(this, order));
        // 7. 返回订单信息
        return getOrderDetail(userId, order.getOrderId());
    }

    /**
     * 按卖家分组购物车商品
     */
    private Map<Integer, List<Cart>> groupCartItemsBySeller(List<Cart> cartItems) {
        Map<Integer, List<Cart>> sellerCartMap = new HashMap<>();

        for (Cart cartItem : cartItems) {
            Product product = productMapper.selectById(cartItem.getProductId());
            if (product == null) {
                throw new BusinessException("商品不存在: " + cartItem.getProductId());
            }

            Integer sellerId = product.getSellerId();
            sellerCartMap.computeIfAbsent(sellerId, k -> new ArrayList<>()).add(cartItem);
        }

        return sellerCartMap;
    }

    /**
     * 验证库存
     */
    private void validateInventory(Map<Integer, List<Cart>> sellerCartMap) {
        for (List<Cart> sellerCartItems : sellerCartMap.values()) {
            for (Cart cartItem : sellerCartItems) {
                Product product = productMapper.selectById(cartItem.getProductId());
                if (product.getStock() < cartItem.getQuantity()) {
                    throw new BusinessException(product.getTitle() + " 库存不足");
                }
            }
        }
    }

    /**
     * 计算总金额
     */
    private BigDecimal calculateTotalAmount(List<Cart> cartItems) {
        return cartItems.stream()
                .map(item -> {
                    Product product = productMapper.selectById(item.getProductId());
                    return product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 处理余额支付
     */
    private void handleBalancePayment(Integer userId, BigDecimal totalAmount) {
        User user = userMapper.selectById(userId);
        if (user.getBalance().compareTo(totalAmount) < 0) {
            throw new BusinessException("余额不足");
        }
        userMapper.updateBalance(userId, user.getBalance().subtract(totalAmount));
    }


    /**
     * 格式化完整地址
     */
    private String formatFullAddress(UserAddress address) {
        return String.format("%s %s %s %s",
                address.getProvince(),
                address.getCity(),
                address.getDistrict(),
                address.getDetailAddress());
    }

    /**
     * 创建支付记录
     */
    private void createPaymentRecord(String orderId, BigDecimal amount, String paymentMethod) {
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus(1); // 1-成功
        payment.setCreatedAt(LocalDateTime.now());
        payment.setPaidAt(LocalDateTime.now());

        paymentMapper.insertPayment(payment);
    }

    /**
     * 创建订单项
     */
    private void createOrderItems(String orderId, List<Cart> cartItems) {
        for (Cart cartItem : cartItems) {
            Product product = productMapper.selectById(cartItem.getProductId());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orderId);
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setProductName(product.getTitle());

            // 使用现有的方法获取商品主图URL
            String mainImageUrl = productMapper.selectMainImageByProductId(cartItem.getProductId());
            orderItem.setProductImage(mainImageUrl);

            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            orderItem.setCondition(product.getCondition());
            orderItem.setItemStatus(1);
            orderItem.setCreatedAt(LocalDateTime.now());

            orderItemMapper.insertOrderItem(orderItem);
        }
    }

    /**
     * 获取商品图片
     */
    private String getProductImage(Product product) {
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            return product.getImages().get(0).getImageUrl();
        }
        return null;
    }

    /**
     * 更新库存、卖家余额和卖家统计信息
     * @param cartItems 购物车项
     * @param sellerId 卖家ID
     */
    private void updateInventoryAndSellerStats(List<Cart> cartItems, Integer sellerId) {
        BigDecimal sellerTotal = BigDecimal.ZERO;
        int totalQuantitySold = 0; // 新增：计算本次订单的总商品件数

        for (Cart cartItem : cartItems) {
            Product product = productMapper.selectById(cartItem.getProductId());

            // 更新库存
            productMapper.updateStock(product.getProductId(), product.getStock() - cartItem.getQuantity());

            // 累计卖家收入
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            sellerTotal = sellerTotal.add(itemTotal);

            // 新增：累计总销量
            totalQuantitySold += cartItem.getQuantity();
        }

        // 更新卖家余额
        User seller = userMapper.selectById(sellerId);
        if (seller != null) {
            userMapper.updateBalance(sellerId, seller.getBalance().add(sellerTotal));
        }


        // 新增：更新卖家总销量
        if (totalQuantitySold > 0) {
            sellerMapper.increaseTotalSales(sellerId, totalQuantitySold);
        }
    }

    /**
     * 计算特定卖家的订单总金额
     */
    private BigDecimal calculateSellerTotalAmount(List<Cart> sellerCartItems) {
        return sellerCartItems.stream()
                .map(item -> {
                    Product product = productMapper.selectById(item.getProductId());
                    return product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 生成订单号
     */
    private String generateOrderId() {
        return System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 修改说明:
     * - 不再使用效率低下的 N+1 查询方式。
     * - 直接调用 `orderMapper.findOrdersByUserId(userId)`，该方法通过单次 JOIN 查询获取所有需要的数据。
     * - 这样可以显著提高页面加载速度，尤其是在订单数量多的时候。
     */
    public List<OrderListDTO> getOrderList(Integer userId) {
        return orderMapper.findOrdersByUserId(userId.longValue());
    }

    public OrderDTO getOrderDetail(Integer userId, String orderId) {
        Order order = orderMapper.selectByOrderId(orderId);
        // 权限校验，用户只能查看自己的订单或自己作为卖家的订单
        if (order == null || (!order.getUserId().equals(userId) && !order.getSellerId().equals(userId))) {
            throw new BusinessException("订单不存在或无权限访问");
        }

        List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
        OrderDTO orderDTO = convertToOrderDTO(order);
        orderDTO.setItems(convertToOrderItemDTOs(items));

        return orderDTO;
    }

    // 该方法已被 getOrderList 替代，可以考虑移除
    public List<OrderListDTO> getUserOrders(Long userId) {
        // 直接调用 Mapper 方法获取所需数据
        return orderMapper.findOrdersByUserId(userId);
    }

    @Transactional
    public void confirmReceive(Integer userId, String orderId) {
        Order order = orderMapper.selectByOrderId(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在或无权限访问");
        }

        if (order.getStatus() != 3) { // 3: 待收货
            throw new BusinessException("订单状态不正确，无法确认收货");
        }

        orderMapper.updateOrderStatus(orderId, 4, LocalDateTime.now(), null); // 4-已完成
    }

    @Transactional
    public void cancelOrder(Integer userId, String orderId) {
        Order order = orderMapper.selectByOrderId(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在或无权限访问");
        }

        // 只有待付款的订单可以由用户取消
        if (order.getStatus() != 1) { // 1: 待付款
            throw new BusinessException("只有待付款的订单才能取消");
        }

        orderMapper.updateOrderStatus(orderId, 5, null, LocalDateTime.now()); // 5-已取消

        // TODO: 在这里添加归还商品库存的逻辑
    }

    // 转换方法
    private OrderListDTO convertToOrderListDTO(Order order) {
        OrderListDTO dto = new OrderListDTO();
        dto.setOrderId(order.getOrderId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUserRemark(order.getUserRemark());
        dto.setShippingCompany(order.getShippingCompany());

        // 获取订单商品项
        List<OrderItem> items = orderItemMapper.selectByOrderId(order.getOrderId());
        dto.setItems(convertToOrderItemDTOs(items));

        return dto;
    }

    private OrderDTO convertToOrderDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setUserId(order.getUserId());
        dto.setSellerId(order.getSellerId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setTransactionId(order.getTransactionId());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setPaidAt(order.getPaidAt());
        dto.setShippedAt(order.getShippedAt());
        dto.setReceivedAt(order.getReceivedAt());
        dto.setCompletedAt(order.getCompletedAt());
        dto.setUserRemark(order.getUserRemark());
        dto.setShippingCompany(order.getShippingCompany());
        dto.setTrackingNumber(order.getTrackingNumber());
        dto.setLogisticsStatus(order.getLogisticsStatus());
        dto.setReceiverName(order.getReceiverName());
        dto.setReceiverPhone(order.getReceiverPhone());
        dto.setShippingAddress(order.getShippingAddress());

        return dto;
    }

    private List<OrderItemDTO> convertToOrderItemDTOs(List<OrderItem> items) {
        if (items == null) {
            return new ArrayList<>();
        }
        return items.stream()
                .map(item -> {
                    OrderItemDTO dto = new OrderItemDTO();
                    dto.setItemId(item.getItemId());
                    dto.setOrderId(item.getOrderId());
                    dto.setProductId(item.getProductId());
                    dto.setProductName(item.getProductName());
                    dto.setProductImage(item.getProductImage());
                    dto.setQuantity(item.getQuantity());
                    dto.setUnitPrice(item.getUnitPrice());
                    dto.setTotalPrice(item.getTotalPrice());
                    dto.setCondition(item.getCondition());
                    dto.setItemStatus(item.getItemStatus());
                    dto.setIsReviewed(item.getIsReviewed());
                    dto.setCreatedAt(item.getCreatedAt());
                    dto.setAfterSaleId(item.getAfterSaleId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 根据卖家ID获取其所有订单
     * @param sellerId 卖家ID
     * @return 订单列表 DTO
     */
    public List<OrderListDTO> getOrdersBySellerId(Integer sellerId) {
        return orderMapper.findOrdersBySellerId(sellerId);
    }

    /**
     * 卖家发货
     * @param sellerId 卖家ID
     * @param orderId 订单ID
     * @param shippingCompany 物流公司
     * @param trackingNumber 物流单号
     */
    @Transactional
    public void shipOrder(Integer sellerId, String orderId, String shippingCompany, String trackingNumber) {
        // 1. 查找订单并进行权限和状态校验
        Order order = orderMapper.selectByOrderId(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getSellerId().equals(sellerId)) {
            throw new BusinessException("无权操作该订单");
        }
        if (order.getStatus() != 2) { // 2-待发货
            throw new BusinessException("订单当前状态不是“待发货”，无法执行发货操作");
        }

        // 2. 更新订单状态和物流信息
        int updatedRows = orderMapper.updateOrderForShipment(
                orderId,
                sellerId,
                shippingCompany,
                trackingNumber,
                LocalDateTime.now()
        );

        if (updatedRows == 0) {
            throw new BusinessException("发货失败，请重试");
        }

        // 3. 【新增逻辑】发布订单已发货事件
        // 更新 order 对象中的物流信息，以便事件监听器能获取到最新数据
        order.setStatus(3); // 待收货
        order.setShippingCompany(shippingCompany);
        order.setTrackingNumber(trackingNumber);
        order.setShippedAt(LocalDateTime.now());

        // 发布事件
        eventPublisher.publishEvent(new OrderShippedEvent(this, order));
    }
}
