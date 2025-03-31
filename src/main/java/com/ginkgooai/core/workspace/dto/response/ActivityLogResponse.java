package com.ginkgooai.core.workspace.dto.response;

import com.ginkgooai.core.common.enums.ActivityType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@Schema(description = "Response containing activity log information")
public class ActivityLogResponse {

    @Schema(description = "Unique identifier of the activity log", example = "550e8400-e29b-41d4-a716-446655440000")
    private String id;

    @Schema(description = "Type of activity that was performed", example = "PROJECT_CREATED")
    private ActivityType activityType;

    @Schema(description = "Human-readable description of the activity", example = "New project 'Avatar 2' was created")
    private String description;

    @Schema(description = "ID of the workspace where the activity occurred", example = "ws-12345")
    private String workspaceId;

    @Schema(description = "ID of the project related to this activity (if applicable)", example = "proj-67890")
    private String projectId;

    @Schema(description = "ID of the application related to this activity (if applicable)", example = "app-12345")
    private String applicationId;

    @Schema(description = "ID of the user who performed the activity", example = "user-12345")
    private String createdBy;

    @Schema(description = "Additional contextual variables related to the activity",
            example = "{\"projectName\": \"Avatar 2\", \"roleName\": \"Jake Sully\"}")
    private Map<String, Object> variables;

    @Schema(description = "Attachments related to the activity (if any)",
            example = "{\"fileUrl\": \"https://example.com/document.pdf\"}")
    private Map<String, Object> attachments;

    @Schema(description = "Timestamp when the activity occurred", example = "2023-04-15T09:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Human-readable relative time when the activity occurred", example = "5 minutes ago")
    private String timeAgo;

    @Schema(description = "Information about the user who performed the activity")
    private UserInfo userInfo;

    @Data
    @Builder
    @Schema(description = "Basic information about a user")
    public static class UserInfo {

        @Schema(description = "Unique identifier of the user", example = "user-12345")
        private String id;

        @Schema(description = "Full name of the user", example = "John Smith")
        private String name;

        @Schema(description = "URL to the user's profile picture", example = "https://example.com/profiles/john.jpg")
        private String picture;
    }
}