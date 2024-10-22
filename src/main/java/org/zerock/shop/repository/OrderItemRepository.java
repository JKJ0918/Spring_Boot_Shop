package org.zerock.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.shop.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
