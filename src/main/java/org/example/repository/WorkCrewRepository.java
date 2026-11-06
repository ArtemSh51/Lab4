package org.example.repository;

import org.example.entity.WorkCrew;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkCrewRepository extends JpaRepository<WorkCrew, Long> {
}