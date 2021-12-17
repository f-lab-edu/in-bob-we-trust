package com.inbobwetrust.repository;

import com.inbobwetrust.model.entity.Order;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface OrderRepository {
    boolean save(Order order);

    Optional<Order> findByOrderId(Long id);

    List<Order> findAll();
}
