package com.coding24h.mall_spring.service;


import com.coding24h.mall_spring.dto.review.ReplySubmitDTO;
import com.coding24h.mall_spring.entity.vo.ReplyVO;

import java.util.List;

/**
 * 回复服务接口
 */
public interface ReplyService {

    /**
     * 提交回复
     */
    boolean submitReply(ReplySubmitDTO replyDTO, Long userId);

    /**
     * 删除回复
     */
    boolean deleteReply(Integer replyId, Long userId);

    /**
     * 获取评价的回复列表
     */
    List<ReplyVO> getRepliesByReviewId(Integer reviewId);

    /**
     * 获取回复详情
     */
    ReplyVO getReplyDetail(Integer replyId);

    /**
     * 检查用户是否已追评
     */
    boolean hasUserAppended(Integer reviewId, Long userId);
}
