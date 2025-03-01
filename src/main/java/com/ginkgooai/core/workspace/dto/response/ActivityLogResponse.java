package com.ginkgooai.core.workspace.dto.response;

import com.ginkgooai.core.common.bean.ActivityType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ActivityLogResponse {
    private String id;
    private ActivityType activityType;
    private String description;
    private String workspaceId;
    private String projectId;
    private String applicationId;
    private String createdBy;
    private Map<String, Object> variables;
    private Map<String, Object> attachments;
    private LocalDateTime createdAt;
    private String timeAgo;
    private UserInfo userInfo;
    
    @Data
    @Builder
    public static class UserInfo {
        private String id;
        private String name;
        private String picture;
    }
}