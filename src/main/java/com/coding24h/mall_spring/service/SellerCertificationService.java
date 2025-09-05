package com.coding24h.mall_spring.service;

import com.coding24h.mall_spring.entity.SellerCertification;
import com.coding24h.mall_spring.entity.vo.PageResult;


public interface SellerCertificationService {

    /**
     * 提交认证申请
     */
    void submitCertification(SellerCertification certification);

    /**
     * 根据用户ID获取认证记录
     */
    SellerCertification getByUserId(Long userId);

    /**
     * 审核认证申请
     */
    void reviewCertification(Integer certificationId, Integer status, Long adminId, String rejectReason);

    /**
     * 获取认证申请列表（管理员）
     */
    PageResult<SellerCertification> getCertificationList(Integer page, Integer size, Integer status);

    /**
     * 检查用户是否已认证
     */
    boolean isUserCertified(Long userId);
}
