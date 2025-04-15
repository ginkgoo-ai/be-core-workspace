package com.ginkgooai.core.workspace.dto.response;

import com.ginkgooai.core.workspace.domain.LogoType;
import com.ginkgooai.core.workspace.domain.Workspace;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing workspace information")
public class WorkspaceResponse {

    @Schema(description = "Unique identifier of the workspace", example = "550e8400-e29b-41d4-a716-446655440000")
    private String id;

    @Schema(description = "Name of the workspace", example = "Production Studio")
    private String name;

    @Schema(description = "Domain of the workspace", example = "production-studio")
    private String domain;

    @Schema(description = "Description of the workspace", example = "Main production workspace for film projects")
    private String description;

    @Schema(description = "Status of the workspace", example = "ACTIVE")
    private String status;

    @Schema(description = "Logo URL of the workspace", example = "https://example.com/logo.png")
    private String logoUrl;

    @Schema(description = "Timestamp when the workspace was created", example = "2023-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "User ID who created the workspace", example = "user-123456")
    private String createdBy;

    @Schema(description = "Timestamp when the workspace was last updated", example = "2023-02-20T14:15:00")
    private LocalDateTime updatedAt;

    @Schema(description = "User ID who last updated the workspace", example = "user-123456")
    private String updatedBy;

    /**
     * Factory method to create a WorkspaceResponse from a Workspace entity
     *
     * @param workspace The workspace entity
     * @return A new WorkspaceResponse object
     */
    public static WorkspaceResponse from(Workspace workspace) {
        return WorkspaceResponse.builder()
                .id(workspace.getId())
                .name(workspace.getName())
			.domain(workspace.getDomain())
                .description(workspace.getDescription())
                .status(workspace.getStatus().name())
                .logoUrl(workspace.getPortalPreviewLogoType() == LogoType.PRIMARY ? workspace.getLogoUrl() : workspace.getSecondaryLogoUrl())
                .createdAt(workspace.getCreatedAt())
                .createdBy(workspace.getCreatedBy())
                .updatedAt(workspace.getUpdatedAt())
                .updatedBy(workspace.getUpdatedBy())
                .build();
    }
}