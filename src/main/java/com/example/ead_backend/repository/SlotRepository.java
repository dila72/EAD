package com.example.ead_backend.repository;

import com.example.ead_backend.model.entity.ServiceSlot;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SlotRepository extends JpaRepository<ServiceSlot, Long> {
    @Query("SELECT s FROM ServiceSlot s WHERE s.available = true")
    List<ServiceSlot> findAvailableSlots();
}