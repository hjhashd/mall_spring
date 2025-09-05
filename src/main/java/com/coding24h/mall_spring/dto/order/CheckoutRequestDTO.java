package com.coding24h.mall_spring.dto.order;


import java.util.List;

public class CheckoutRequestDTO {
    private String paymentMethod;      // 支付方式：wechat, alipay, balance
    private Integer addressId;         // 收货地址ID（改为地址ID）
    private String remark;             // 订单备注
    private String shippingCompany;    // 物流公司名称

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getShippingCompany() {
        return shippingCompany;
    }

    public void setShippingCompany(String shippingCompany) {
        this.shippingCompany = shippingCompany;
    }
}
