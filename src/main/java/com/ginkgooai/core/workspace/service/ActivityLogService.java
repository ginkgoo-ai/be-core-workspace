package com.ginkgooai.core.workspace.service;

import com.ginkgooai.core.common.bean.ActivityType;
import com.ginkgooai.core.common.exception.ResourceNotFoundException;
import com.ginkgooai.core.common.utils.TimeUtils;
import com.ginkgooai.core.workspace.client.identity.UserClient;
import com.ginkgooai.core.workspace.client.identity.dto.UserInfo;
import com.ginkgooai.core.workspace.domain.ActivityLog;
import com.ginkgooai.core.workspace.dto.request.ActivityQueryRequest;
import com.ginkgooai.core.workspace.dto.response.ActivityLogResponse;
import com.ginkgooai.core.workspace.repository.ActivityLogRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final UserClient userClient; 
    
    public String formatActivityDescription(String template, Map<String, Object> variables) {
        for (String variable : variables.keySet()) {
            Object value = variables.get(variable);
            if (value != null) {
                template = template.replace("{" + variable + "}", value.toString());
            }
        }
        return template;
    }

    /**
     * Batch create activity logs
     */
    @Transactional
    public List<ActivityLog> createLogs(List<ActivityLog> requests) {
        List<ActivityLog> logs = requests.stream()
                .map(request -> ActivityLog.builder()
                        .activityType(request.getActivityType())
                        .description(request.getDescription())
                        .workspaceId(request.getWorkspaceId())
                        .projectId(request.getProjectId())
                        .applicationId(request.getApplicationId())
                        .variables(request.getVariables())
                        .attachments(request.getAttachments())
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
    public Page<ActivityLogResponse> search(ActivityQueryRequest searchRequest, Pageable pageable) {
        Specification<ActivityLog> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(searchRequest.getWorkspaceId())) {
                predicates.add(cb.equal(root.get("workspaceId"), searchRequest.getWorkspaceId()));
            }

            if (StringUtils.hasText(searchRequest.getProjectId())) {
                predicates.add(cb.equal(root.get("projectId"), searchRequest.getProjectId()));
            }

            if (StringUtils.hasText(searchRequest.getRoleId())) {
                Predicate roleIdInVariables = cb.equal(
                        cb.function(
                                "jsonb_extract_path_text",
                                String.class,
                                root.get("variables"),
                                cb.literal("roleId")
                        ),
                        searchRequest.getRoleId()
                );

                Predicate roleRelatedActivities = root.get("activityType").in(
                        Arrays.asList(
                                ActivityType.ROLE_CREATED,
                                ActivityType.ROLE_STATUS_UPDATE
                        )
                );

                predicates.add(cb.and(roleRelatedActivities, roleIdInVariables));
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

        Page<ActivityLog> activityLogs = activityLogRepository.findAll(spec, pageable);
        return activityLogs.map(this::convertToResponse);
    }

    /**
     * Convert ActivityLog to ActivityLogResponse with enhanced information
     */
    private ActivityLogResponse convertToResponse(ActivityLog log) {
        UserInfo userInfo = userClient.getUserById(log.getCreatedBy()).getBody();
        String timeAgo = TimeUtils.getTimeAgo(log.getCreatedAt());

        Map<String, Object> variables = log.getVariables();
        variables.put("user", userInfo.getName());
        String description = formatActivityDescription(log.getDescription(), log.getVariables());

        return ActivityLogResponse.builder()
                .id(log.getId())
                .activityType(log.getActivityType())
                .description(description)
                .workspaceId(log.getWorkspaceId())
                .projectId(log.getProjectId())
                .applicationId(log.getApplicationId())
                .variables(log.getVariables())
                .attachments(log.getAttachments())
                .createdBy(log.getCreatedBy())
                .createdAt(log.getCreatedAt())
                .timeAgo(timeAgo)
                .userInfo(ActivityLogResponse.UserInfo.builder()
                        .id(userInfo.getId())
                        .name(userInfo.getLastName())
                        .picture(userInfo.getPicture())
                        .build())
                .build();
    }

    /**
     * Delete log by id
     */
    public void deleteLog(String id) {
        activityLogRepository.deleteById(id);
    }
}