package com.shopme.order;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopme.common.entity.order.Order;

public interface OrderReopositary extends JpaRepository<Order, Integer> {

}
