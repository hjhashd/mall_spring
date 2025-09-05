package com.coding24h.mall_spring.dto;

/**
 * WebSocket 通知载体 DTO
 */
public class NotificationDTO<T> {

    /**
     * 通知类型, e.g., "CHAT_MESSAGE", "ORDER_SHIPPED"
     */
    private String type;

    /**
     * 通知数据体
     */
    private T data;

    // 无参构造函数
    public NotificationDTO() {
    }

    // 有参构造函数
    public NotificationDTO(String type, T data) {
        this.type = type;
        this.data = data;
    }

    // type 的 getter 和 setter
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // data 的 getter 和 setter
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    // toString 方法
    @Override
    public String toString() {
        return "NotificationDTO{" +
                "type='" + type + '\'' +
                ", data=" + data +
                '}';
    }

    // equals 方法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NotificationDTO<?> that = (NotificationDTO<?>) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        return data != null ? data.equals(that.data) : that.data == null;
    }

    // hashCode 方法
    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }
}
