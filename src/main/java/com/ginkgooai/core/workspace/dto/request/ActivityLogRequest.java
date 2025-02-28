package com.ginkgooai.core.workspace.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogRequest {
    private String activityType;
    private String description;
    private String workspaceId;
    private String projectId;
    private String applicationId;
    private Map<String, Object> context;
    private String status;
    private String createdBy;
}