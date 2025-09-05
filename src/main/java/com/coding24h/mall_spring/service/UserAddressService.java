package com.coding24h.mall_spring.service;

import com.coding24h.mall_spring.entity.UserAddress;
import com.coding24h.mall_spring.mapper.UserAddressMapper;
import com.coding24h.mall_spring.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserAddressService {

    @Autowired
    private UserAddressMapper userAddressMapper;

    /**
     * 获取用户地址列表
     */
    public List<UserAddress> getUserAddresses(Integer userId) {
        return userAddressMapper.selectByUserId(userId);
    }

    /**
     * 获取地址详情
     */
    public UserAddress getAddressById(Integer addressId, Integer userId) {
        UserAddress address = userAddressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在或无权限访问");
        }
        return address;
    }

    /**
     * 新增地址
     */
    @Transactional
    public void addAddress(UserAddress address) {
        if (address.getIsDefault() != null && address.getIsDefault()) {
            userAddressMapper.clearDefaultAddress(address.getUserId());
        }

        address.setCreatedAt(LocalDateTime.now());
        address.setUpdatedAt(LocalDateTime.now());
        userAddressMapper.insertAddress(address);
    }

    /**
     * 更新地址
     */
    @Transactional
    public void updateAddress(Integer addressId, UserAddress address, Integer userId) {
        UserAddress existingAddress = getAddressById(addressId, userId);

        // 如果设为默认地址，先取消其他默认地址
        if (address.getIsDefault() && !existingAddress.getIsDefault()) {
            userAddressMapper.clearDefaultAddress(userId);
        }

        address.setAddressId(addressId);
        address.setUserId(userId);
        address.setUpdatedAt(LocalDateTime.now());

        userAddressMapper.updateAddress(address);
    }

    /**
     * 删除地址
     */
    @Transactional
    public void deleteAddress(Integer addressId, Integer userId) {
        UserAddress address = getAddressById(addressId, userId);
        userAddressMapper.deleteAddress(addressId);
    }

    /**
     * 设为默认地址
     */
    @Transactional
    public void setDefaultAddress(Integer addressId, Integer userId) {
        UserAddress address = getAddressById(addressId, userId);

        // 先取消其他默认地址
        userAddressMapper.clearDefaultAddress(userId);

        // 设置当前地址为默认
        userAddressMapper.setDefaultAddress(addressId);
    }

    /**
     * 获取默认地址
     */
    public UserAddress getDefaultAddress(Integer userId) {
        return userAddressMapper.selectDefaultByUserId(userId);
    }

    /**
     * (新增) 管理员获取所有用户地址列表
     */
    public List<UserAddress> getAllUserAddressesForAdmin() {
        return userAddressMapper.selectAllWithUserDetails();
    }

    /**
     * [Admin] 更新任意地址 (无用户ID校验)
     */
    @Transactional
    public void updateAddressByAdmin(Integer addressId, UserAddress address) {
        // 检查地址是否存在
        UserAddress existingAddress = userAddressMapper.selectById(addressId);
        if (existingAddress == null) {
            throw new BusinessException("要更新的地址不存在");
        }

        // 如果将此地址设为默认，需要先清除该用户的所有其他默认地址
        if (address.getIsDefault() && !existingAddress.getIsDefault()) {
            userAddressMapper.clearDefaultAddress(existingAddress.getUserId());
        }

        address.setAddressId(addressId);
        address.setUpdatedAt(LocalDateTime.now());
        userAddressMapper.updateAddress(address);
    }

    /**
     * [Admin] 删除任意地址 (无用户ID校验)
     */
    @Transactional
    public void deleteAddressByAdmin(Integer addressId) {
        // 检查地址是否存在
        UserAddress existingAddress = userAddressMapper.selectById(addressId);
        if (existingAddress == null) {
            throw new BusinessException("要删除的地址不存在");
        }
        userAddressMapper.deleteAddress(addressId);
    }
}
