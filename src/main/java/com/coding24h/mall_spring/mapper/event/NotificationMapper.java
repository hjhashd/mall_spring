package com.coding24h.mall_spring.mapper.event;

import com.coding24h.mall_spring.entity.event.MyNotification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知模块的MyBatis Mapper接口
 */
@Mapper
public interface NotificationMapper {

    /**
     * 插入一条新的通知记录
     * @param notification 通知对象
     * @return 返回影响的行数
     */
    int insert(MyNotification notification);

    /**
     * 根据用户ID查询所有未读通知
     * @param userId 用户ID
     * @return 未读通知列表
     */
    List<MyNotification> findUnreadByUserId(@Param("userId") Integer userId);

    /**
     * 根据用户ID查询所有通知（可以分页）
     * @param userId 用户ID
     * @return 该用户的所有通知列表
     */
    List<MyNotification> findAllByUserId(@Param("userId") Integer userId);


    /**
     * 将指定ID的通知标记为已读
     * @param notificationId 通知ID
     * @return 返回影响的行数
     */
    int markAsRead(@Param("notificationId") Integer notificationId);

    /**
     * 将某个用户的所有未读通知标记为已读
     * @param userId 用户ID
     * @return 返回影响的行数
     */
    int markAllAsReadByUserId(@Param("userId") Integer userId);
}
