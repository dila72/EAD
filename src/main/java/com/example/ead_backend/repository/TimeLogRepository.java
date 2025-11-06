package com.example.ead_backend.repository;

import com.example.ead_backend.model.entity.TimeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;


@Repository
public interface TimeLogRepository extends JpaRepository<TimeLog, String> {
    List<TimeLog> findByDate(LocalDate date);

    List<TimeLog> findByDateAndType(LocalDate date, String type);
}
