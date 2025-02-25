package com.ginkgooai.core.workspace.controller;

import com.ginkgooai.core.common.bean.ActivityType;
import com.ginkgooai.core.workspace.domain.ActivityLog;
import com.ginkgooai.core.workspace.dto.ActivityLogResponse;
import com.ginkgooai.core.workspace.dto.ActivityQueryRequest;
import com.ginkgooai.core.workspace.service.ActivityLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    @PreAuthorize("hasPermission(#workspaceId, 'WORKSPACE', 'READ')")
    public Page<ActivityLogResponse> searchActivityLogs(
            @RequestParam(required = false) String workspaceId,
            @RequestParam(required = false) String projectId,
            @RequestParam(required = false) String applicationId,
            @RequestParam(required = false) String activityType,
            @RequestParam(required = false) String createdBy,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startTime,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endTime,
            @ParameterObject Pageable pageable
    ) {
        Page<ActivityLog> page = activityLogService.search(ActivityQueryRequest.builder()
                .workspaceId(workspaceId)
                .projectId(projectId)
                .applicationId(applicationId)
                .activityType(activityType)
                .createdBy(createdBy)
                .startTime(startTime)
                .endTime(endTime)
                .build(), pageable);

        return page.map(ActivityLogResponse::from);
    }

    @GetMapping("/types")
    @Operation(summary = "Get all activity types")
    public List<String> getActivityTypes() {
        return Arrays.stream(ActivityType.values())
                .map(ActivityType::name)
                .collect(Collectors.toList());
    }
}