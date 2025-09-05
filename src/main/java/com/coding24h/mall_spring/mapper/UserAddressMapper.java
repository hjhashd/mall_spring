package com.coding24h.mall_spring.mapper;

import com.coding24h.mall_spring.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserAddressMapper {

    List<UserAddress> selectByUserId(@Param("userId") Integer userId);

    UserAddress selectById(@Param("addressId") Integer addressId);

    UserAddress selectDefaultByUserId(@Param("userId") Integer userId);

    int insertAddress(UserAddress address);

    int updateAddress(UserAddress address);

    int deleteAddress(@Param("addressId") Integer addressId);

    int clearDefaultAddress(@Param("userId") Integer userId);

    int setDefaultAddress(@Param("addressId") Integer addressId);
    List<UserAddress> selectAllWithUserDetails();

}
