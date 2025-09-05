package com.coding24h.mall_spring.mapper;

import com.coding24h.mall_spring.entity.SellerCertification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SellerCertificationMapper {

    /**
     * 插入新的认证申请
     */
    int insert(SellerCertification certification);

    /**
     * 根据用户ID查询认证记录
     */
    SellerCertification selectByUserId(Long userId);

    /**
     * 根据ID查询认证记录
     */
    SellerCertification selectById(Integer certificationId);

    /**
     * 更新认证状态
     */
    int updateStatus(@Param("certificationId") Integer certificationId,
                     @Param("status") Integer status,
                     @Param("reviewedBy") Long reviewedBy,
                     @Param("rejectReason") String rejectReason);

    /**
     * 分页查询认证记录列表
     */
    List<SellerCertification> selectList(@Param("offset") Integer offset,
                                         @Param("limit") Integer limit,
                                         @Param("status") Integer status);

    /**
     * 统计总数
     */
    int countTotal(@Param("status") Integer status);

    /**
     * 检查用户是否已有认证记录
     */
    boolean existsByUserId(Long userId);
}
