package com.coding24h.mall_spring.mapper;

import com.coding24h.mall_spring.entity.ContentModeration;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ContentModerationMapper {


/**
 * 插入内容审核信息的方法
 * @param moderation 内容审核对象，包含需要审核的内容信息
 */
    void insertModeration(ContentModeration moderation);

    /**
     * 更新审核状态
     * @param contentId 内容ID
     * @param status 审核状态
     * @param reason 审核原因
     * @param moderatorId 审核员ID
     * @return 更新的记录数
     */
    int updateModerationStatus(@Param("contentId") Long contentId,
                               @Param("status") Integer status,
                               @Param("reason") String reason,
                               @Param("moderatorId") Long moderatorId);

    ContentModeration getModerationByContentId(Integer contentId);

    void updateModeration(ContentModeration moderation);

    Integer getStatusByContentIdAndType(@Param("contentId") Integer contentId, @Param("contentType") String contentType);

}
