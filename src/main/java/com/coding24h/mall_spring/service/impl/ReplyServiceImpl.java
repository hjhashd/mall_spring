package com.coding24h.mall_spring.service.impl;

import com.coding24h.mall_spring.dto.review.ReplySubmitDTO;
import com.coding24h.mall_spring.entity.Reply;
import com.coding24h.mall_spring.entity.vo.ReplyVO;
import com.coding24h.mall_spring.mapper.review.ReplyMapper;
import com.coding24h.mall_spring.mapper.review.ReviewMapper;
import com.coding24h.mall_spring.service.ReplyService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 回复服务实现类
 */
@Service
public class ReplyServiceImpl implements ReplyService {

    private final ReplyMapper replyMapper;
    private final ReviewMapper reviewMapper;

    // 构造函数注入
    public ReplyServiceImpl(ReplyMapper replyMapper, ReviewMapper reviewMapper) {
        this.replyMapper = replyMapper;
        this.reviewMapper = reviewMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitReply(ReplySubmitDTO replyDTO, Long userId) {
        try {
            // 创建回复实体
            Reply reply = new Reply();
            reply.setReviewId(replyDTO.getReviewId());
            reply.setUserId(userId.intValue());
            reply.setContent(replyDTO.getContent());
            reply.setRepliedToUserId(replyDTO.getRepliedToUserId());
            reply.setRepliedToUsername(replyDTO.getRepliedToUsername());
            reply.setIsAppend(replyDTO.getIsAppend());
            reply.setCreateTime(LocalDateTime.now());
            reply.setDeleted(false);

            // 保存回复
            int result = replyMapper.insertReply(reply);

            // 如果是追评，更新评价的追评状态
            if (Boolean.TRUE.equals(replyDTO.getIsAppend())) {
                reviewMapper.updateAppendStatus(replyDTO.getReviewId(), true);
            }

            return result > 0;

        } catch (Exception e) {
            System.err.println("提交回复失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("提交回复失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteReply(Integer replyId, Long userId) {
        try {
            // 检查权限
            Reply reply = replyMapper.selectReplyById(replyId);
            if (reply == null || !reply.getUserId().equals(userId.intValue())) {
                return false;
            }

            // 逻辑删除
            int result = replyMapper.deleteReplyById(replyId);

            // 如果是追评，需要更新评价的追评状态
            if (Boolean.TRUE.equals(reply.getIsAppend())) {
                // 检查是否还有其他追评
                Reply appendReply = replyMapper.selectAppendReplyByReviewIdAndUserId(
                        reply.getReviewId(), userId.intValue());
                if (appendReply == null) {
                    reviewMapper.updateAppendStatus(reply.getReviewId(), false);
                }
            }

            return result > 0;

        } catch (Exception e) {
            System.err.println("删除回复失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("删除回复失败: " + e.getMessage());
        }
    }

    @Override
    public List<ReplyVO> getRepliesByReviewId(Integer reviewId) {
        List<Reply> replies = replyMapper.selectRepliesByReviewId(reviewId);
        return replies.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public ReplyVO getReplyDetail(Integer replyId) {
        Reply reply = replyMapper.selectReplyById(replyId);
        if (reply == null) {
            return null;
        }
        return convertToVO(reply);
    }

    @Override
    public boolean hasUserAppended(Integer reviewId, Long userId) {
        Reply reply = replyMapper.selectAppendReplyByReviewIdAndUserId(reviewId, userId.intValue());
        return reply != null;
    }

    /**
     * 转换为VO
     */
    private ReplyVO convertToVO(Reply reply) {
        ReplyVO vo = new ReplyVO();
        BeanUtils.copyProperties(reply, vo);
        return vo;
    }
}
