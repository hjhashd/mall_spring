package com.coding24h.mall_spring.mapper;


import com.coding24h.mall_spring.entity.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PaymentMapper {

    int insertPayment(Payment payment);

    Payment selectByOrderId(@Param("orderId") String orderId);
}