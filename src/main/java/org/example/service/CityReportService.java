package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.CityReportDto;
import org.example.entity.CityObject;
import org.example.entity.CityReport;
import org.example.entity.ReportStatus;
import org.example.entity.Role;
import org.example.entity.User;
import org.example.repository.CityObjectRepository;
import org.example.repository.CityReportRepository;
import org.example.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CityReportService {

    private final CityReportRepository repository;
    private final CityObjectRepository cityObjectRepository;
    private final UserRepository userRepository;

    public List<CityReport> findAll() {
        return repository.findAll();
    }

    public CityReport findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("CityReport not found with id: " + id));
    }

    @Transactional
    public CityReport create(CityReportDto dto) {
        CityReport report = new CityReport();
        report.setDescription(dto.getDescription());
        report.setCreatedAt(LocalDateTime.now());
        report.setStatus(ReportStatus.NEW);

        CityObject obj = cityObjectRepository.findById(dto.getCityObjectId())
                .orElseThrow(() -> new RuntimeException("CityObject not found"));
        report.setCityObject(obj);

        User author = resolveAuthor(dto.getAuthorId());
        report.setAuthor(author);

        return repository.save(report);
    }

    @Transactional
    public CityReport update(Long id, CityReportDto dto) {
        CityReport report = findById(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = hasRole(auth, "ROLE_ADMIN");
        boolean isDispatcher = hasRole(auth, "ROLE_DISPATCHER");

        if (!isAdmin && !isDispatcher) {
            boolean isAuthor = report.getAuthor().getEmail().equalsIgnoreCase(auth.getName());
            if (!isAuthor) {
                throw new AccessDeniedException("Изменять обращение может только автор");
            }
            if (report.getStatus() == ReportStatus.RESOLVED
                    || report.getStatus() == ReportStatus.REJECTED) {
                throw new AccessDeniedException("Обращение в финальном статусе нельзя редактировать");
            }
        }

        if (dto.getDescription() != null) report.setDescription(dto.getDescription());

        if (dto.getCityObjectId() != null) {
            CityObject obj = cityObjectRepository.findById(dto.getCityObjectId())
                    .orElseThrow(() -> new RuntimeException("CityObject not found"));
            report.setCityObject(obj);
        }
        if (dto.getAuthorId() != null) {
            if (!isAdmin) {
                throw new AccessDeniedException("Менять автора может только администратор");
            }
            User author = userRepository.findById(dto.getAuthorId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            report.setAuthor(author);
        }

        return repository.save(report);
    }

    @Transactional
    public void delete(Long id) {
        CityReport report = findById(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = hasRole(auth, "ROLE_ADMIN");
        boolean isAuthor = report.getAuthor().getEmail().equalsIgnoreCase(auth.getName());

        if (!isAdmin && !isAuthor) {
            throw new AccessDeniedException("Удалить обращение может только автор или администратор");
        }
        if (report.getStatus() == ReportStatus.IN_PROGRESS) {
            throw new IllegalStateException("Нельзя удалить обращение, по которому идёт работа");
        }
        repository.deleteById(id);
    }

    @Transactional
    public CityReport rejectReport(Long id) {
        CityReport report = findById(id);

        if (report.getStatus() == ReportStatus.RESOLVED) {
            throw new RuntimeException("Нельзя отклонить обращение, так как проблема уже решена (RESOLVED)");
        }
        if (report.getStatus() == ReportStatus.REJECTED) {
            throw new RuntimeException("Обращение уже было отклонено ранее");
        }

        report.setStatus(ReportStatus.REJECTED);
        return repository.save(report);
    }

    private User resolveAuthor(Long authorId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (hasRole(auth, "ROLE_ADMIN") && authorId != null) {
            return userRepository.findById(authorId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        return userRepository.findByEmailIgnoreCase(auth.getName())
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    private boolean hasRole(Authentication auth, String role) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }
}