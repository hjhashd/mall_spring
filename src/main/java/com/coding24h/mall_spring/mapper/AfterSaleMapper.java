package com.coding24h.mall_spring.mapper;

import com.coding24h.mall_spring.dto.order.AfterSaleApplicationDTO;
import com.coding24h.mall_spring.entity.AfterSales;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

import java.util.List;

@Mapper
public interface AfterSaleMapper {

    void insert(AfterSales afterSale);

    AfterSales selectById(Integer afterSaleId);

    void updateStatus(AfterSales afterSale);

    List<AfterSaleApplicationDTO> findAfterSaleApplicationsBySellerId(Integer sellerId);

    /**
     * 查询所有待平台处理的售后申请
     * @return 待处理的售后申请列表
     */
    List<AfterSaleApplicationDTO> findPendingAdminApplications();

    /**
     * 查询所有售后申请
     * @return 所有售后申请列表
     */
    List<AfterSaleApplicationDTO> findAllAdminApplications();

    /**
     * 根据状态查询售后申请
     * @param status 状态码
     * @return 指定状态的售后申请列表
     */
    List<AfterSaleApplicationDTO> findApplicationsByStatus(Integer status);
}
