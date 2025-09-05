package com.coding24h.mall_spring.service.impl;

import com.coding24h.mall_spring.entity.Seller;
import com.coding24h.mall_spring.entity.SellerCertification;
import com.coding24h.mall_spring.entity.vo.PageResult;
import com.coding24h.mall_spring.mapper.SellerCertificationMapper;
import com.coding24h.mall_spring.mapper.SellerMapper;
import com.coding24h.mall_spring.service.SellerCertificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SellerCertificationServiceImpl implements SellerCertificationService {

    @Autowired
    private SellerCertificationMapper sellerCertificationMapper;

    @Autowired
    private SellerMapper sellerMapper;

    @Override
    @Transactional
    public void submitCertification(SellerCertification certification) {
        // 检查用户是否已有认证记录
        if (sellerCertificationMapper.existsByUserId(certification.getUserId())) {
            throw new RuntimeException("您已有认证记录，请勿重复提交");
        }

        // 设置创建时间
        if (certification.getCreatedAt() == null) {
            certification.setCreatedAt(new java.util.Date());
        }

        // 插入认证信息
        sellerCertificationMapper.insert(certification);

        // 同时插入基础卖家信息
        createBasicSellerInfo(certification);
    }

    /**
     * 创建基础卖家信息
     */
    private void createBasicSellerInfo(SellerCertification certification) {
        // 检查是否已存在
        if (sellerMapper.existsBySellerId(certification.getUserId().intValue())) {
            return;
        }

        Seller seller = new Seller();
        seller.setSellerId(certification.getUserId().intValue()); // 转换为Integer
        seller.setShopName(certification.getBusinessName());
        seller.setDescription(certification.getBusinessDescription());
        seller.setLocation(certification.getBusinessAddress());
        seller.setContactPhone(certification.getContactPhone());
        seller.setContactEmail(certification.getContactEmail());
        seller.setIsVerified(false);

        try {
            sellerMapper.insertBasicInfo(seller);
        } catch (Exception e) {
            System.out.println("创建基础卖家信息失败: " + e.getMessage());
        }
    }

    @Override
    public SellerCertification getByUserId(Long userId) {
        return sellerCertificationMapper.selectByUserId(userId);
    }

    @Override
    public void reviewCertification(Integer certificationId, Integer status, Long adminId, String rejectReason) {
        SellerCertification certification = sellerCertificationMapper.selectById(certificationId);
        if (certification == null) {
            throw new RuntimeException("认证记录不存在");
        }

        if (certification.getStatus() != 0) {
            throw new RuntimeException("该认证申请已被处理");
        }

        sellerCertificationMapper.updateStatus(certificationId, status, adminId, rejectReason);

        // 如果认证通过，这里可以更新用户角色为商家
        // 需要根据您的用户角色管理逻辑来实现
    }

    @Override
    public PageResult<SellerCertification> getCertificationList(Integer page, Integer size, Integer status) {
        int offset = (page - 1) * size;

        List<SellerCertification> items = sellerCertificationMapper.selectList(offset, size, status);
        long total = sellerCertificationMapper.countTotal(status);

        // 使用您现有的PageResult类，字段名为total和items
        return new PageResult<>(total, items);
    }

    @Override
    public boolean isUserCertified(Long userId) {
        SellerCertification certification = getByUserId(userId);
        return certification != null && certification.getStatus() == 1;
    }
}
