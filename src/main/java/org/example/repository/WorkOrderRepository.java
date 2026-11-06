package org.example.repository;

import org.example.entity.OrderStatus;
import org.example.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    long countByCrewIdAndStatusIn(Long crewId, Collection<OrderStatus> statuses);
}