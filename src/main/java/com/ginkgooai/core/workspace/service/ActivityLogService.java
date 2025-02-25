package com.ginkgooai.core.workspace.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ginkgooai.core.common.bean.ActivityType;
import com.ginkgooai.core.common.exception.ResourceNotFoundException;
import com.ginkgooai.core.workspace.domain.ActivityLog;
import com.ginkgooai.core.workspace.dto.ActivityLogRequest;
import com.ginkgooai.core.workspace.dto.ActivityQueryRequest;
import com.ginkgooai.core.workspace.repository.ActivityLogRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final ObjectMapper objectMapper;

    /**
     * Create activity log
     */
    public ActivityLog createLog(ActivityLogRequest request) {
        try {
            ActivityLog log = ActivityLog.builder()
                    .id(UUID.randomUUID().toString())
                    .activityType(ActivityType.valueOf(request.getActivityType()))
                    .description(request.getDescription())
                    .workspaceId(request.getWorkspaceId())
                    .projectId(request.getProjectId())
                    .applicationId(request.getApplicationId())
                    .context(request.getContext())
                    .createdBy(request.getCreatedBy())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            return activityLogRepository.save(log);
        } catch (Exception e) {
            log.error("Failed to create activity log", e);
            throw new RuntimeException("Failed to create activity log", e);
        }
    }

    /**
     * Batch create activity logs
     */
    public List<ActivityLog> createLogs(List<ActivityLogRequest> requests) {
        List<ActivityLog> logs = requests.stream()
                .map(request -> ActivityLog.builder()
                        .id(UUID.randomUUID().toString())
                        .activityType(ActivityType.valueOf(request.getActivityType()))
                        .description(request.getDescription())
                        .workspaceId(request.getWorkspaceId())
                        .projectId(request.getProjectId())
                        .applicationId(request.getApplicationId())
                        .context(request.getContext())
                        .createdBy(request.getCreatedBy())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());

        return activityLogRepository.saveAll(logs);
    }

    /**
     * Get log by id
     */
    public ActivityLog getLogById(String id) {
        return activityLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity log", "id", id));
    }

    /**
     * Search logs with conditions
     */
    public Page<ActivityLog> search(ActivityQueryRequest searchRequest, Pageable pageable) {
        Specification<ActivityLog> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(searchRequest.getWorkspaceId())) {
                predicates.add(cb.equal(root.get("workspaceId"), searchRequest.getWorkspaceId()));
            }

            if (StringUtils.hasText(searchRequest.getProjectId())) {
                predicates.add(cb.equal(root.get("projectId"), searchRequest.getProjectId()));
            }

            if (StringUtils.hasText(searchRequest.getApplicationId())) {
                predicates.add(cb.equal(root.get("applicationId"), searchRequest.getApplicationId()));
            }

            if (StringUtils.hasText(searchRequest.getActivityType())) {
                predicates.add(cb.equal(root.get("activityType"), searchRequest.getActivityType()));
            }

            if (StringUtils.hasText(searchRequest.getStatus())) {
                predicates.add(cb.equal(root.get("status"), searchRequest.getStatus()));
            }

            if (searchRequest.getStartTime() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), searchRequest.getStartTime()));
            }

            if (searchRequest.getEndTime() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), searchRequest.getEndTime()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return activityLogRepository.findAll(spec, pageable);
    }

    /**
     * Update log context
     */
    public ActivityLog updateLogContext(String id, Map<String, Object> contextUpdate) {
        ActivityLog log = getLogById(id);
        Map<String, Object> currentContext = log.getContext();
        if (currentContext == null) {
            currentContext = new HashMap<>();
        }
        currentContext.putAll(contextUpdate);
        log.setContext(currentContext);
        log.setUpdatedAt(LocalDateTime.now());
        return activityLogRepository.save(log);
    }

    /**
     * Delete log by id
     */
    public void deleteLog(String id) {
        activityLogRepository.deleteById(id);
    }
}