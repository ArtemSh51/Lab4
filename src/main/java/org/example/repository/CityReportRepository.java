package org.example.repository;

import org.example.entity.CityReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityReportRepository extends JpaRepository<CityReport, Long> {
}