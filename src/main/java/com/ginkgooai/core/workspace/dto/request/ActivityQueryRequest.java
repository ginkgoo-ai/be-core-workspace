package com.ginkgooai.core.workspace.dto.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ActivityQueryRequest {
    private String workspaceId;
    private String projectId;
    private String applicationId;
    private String roleId;
    private String activityType;
    private String status;
    private String createdBy;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    @Builder.Default
    private int pageNo = 1;
    
    @Builder.Default
    private int pageSize = 20;
}
