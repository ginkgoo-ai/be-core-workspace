package com.ginkgooai.core.workspace.controller;

import com.ginkgooai.core.common.enums.ActivityType;
import com.ginkgooai.core.common.utils.ContextUtils;
import com.ginkgooai.core.workspace.dto.request.ActivityQueryRequest;
import com.ginkgooai.core.workspace.dto.response.ActivityLogResponse;
import com.ginkgooai.core.workspace.service.ActivityLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Activity log query API
 */
@RestController
@RequestMapping("/activity-logs")
@Tag(name = "Activity Logs", description = "Activity logs management API")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @GetMapping
    @Operation(summary = "Search activity logs")
    public Page<ActivityLogResponse> searchActivityLogs(@RequestParam(required = false) String projectId,
                                                        @RequestParam(required = false) String applicationId,
                                                        @RequestParam(required = false) String activityType,
                                                        @RequestParam(required = false) String createdBy,
                                                        @RequestParam(required = false)
                                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                        LocalDateTime startTime,
                                                        @RequestParam(required = false)
                                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                        LocalDateTime endTime,
                                                        @Parameter(description = "Page number (zero-based)", example = "0") @RequestParam(defaultValue = "0") int page,
                                                        @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int size,
                                                        @Parameter(description = "Sort direction (ASC/DESC)", example = "DESC") @RequestParam(defaultValue = "DESC") String sortDirection,
                                                        @Parameter(description = "Sort field (e.g., updatedAt)", example = "updatedAt") @RequestParam(defaultValue = "createdAt") String sortField) {
        if (ObjectUtils.isEmpty(ContextUtils.getWorkspaceId())) {
            throw new AuthorizationDeniedException("No workspace chosen");
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ActivityLogResponse> pageResult = activityLogService.search(ActivityQueryRequest.builder()
            .projectId(projectId)
            .applicationId(applicationId)
            .activityType(activityType)
            .createdBy(createdBy)
            .startTime(startTime)
            .endTime(endTime)
            .build(), pageable);

        return pageResult;
    }

    @GetMapping("/types")
    @Operation(summary = "Get all activity types")
	public Map<String, String> getActivityTypes() {
		Map<String, String> activityTypesMap = new HashMap<>();

		Arrays.stream(ActivityType.values()).forEach(activityType -> {
			activityTypesMap.put(activityType.name(), activityType.getDescription());
		});

		return activityTypesMap;
    }


    @GetMapping("/count")
    @Operation(summary = "Count activity logs by time range")
    public Map<String, Long> countActivityLogs(@RequestParam(required = false) String projectId,
                                               @RequestParam(required = false) String applicationId,
                                               @RequestParam(required = false) String activityType,
                                               @RequestParam(required = false)
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                               LocalDateTime startTime,
                                               @RequestParam(required = false)
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                               LocalDateTime endTime) {

        if (ObjectUtils.isEmpty(ContextUtils.getWorkspaceId())) {
            throw new AuthorizationDeniedException("No workspace chosen");
        }

        if (startTime == null) {
            startTime = LocalDateTime.now().minusDays(1);
        }

        if (endTime == null) {
            endTime = LocalDateTime.now();
        }

        long count = activityLogService.countActivityLogs(
            ContextUtils.getWorkspaceId(),
            startTime,
            endTime,
            projectId,
            applicationId,
            activityType
        );

        return Map.of("count", count);
    }

}