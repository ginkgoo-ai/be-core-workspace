package com.ginkgooai.core.workspace.dto;

import com.ginkgooai.core.workspace.domain.ActivityLog;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ActivityLogResponse {
    private String id;
    private String workspaceId;
    private String projectId;
    private String applicationId;
    private String activityType;
    private String description;
    private String operatorId;
    private String operatorName;
    private LocalDateTime occurredAt;
    private Map<String, Object> context;
    
    public static ActivityLogResponse from(ActivityLog activityLog) {
        return ActivityLogResponse.builder()
                .activityType(activityLog.getActivityType().name())
                .description(activityLog.getDescription())
                .workspaceId(activityLog.getWorkspaceId())
                .projectId(activityLog.getProjectId())
                .applicationId(activityLog.getApplicationId())
                .context(activityLog.getContext())
                .build();
    }
}