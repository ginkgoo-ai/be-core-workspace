package com.ginkgooai.core.workspace.dto.response;

import com.ginkgooai.core.workspace.domain.LogoType;
import com.ginkgooai.core.workspace.domain.Workspace;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Response containing workspace settings and configuration details")
public class WorkspaceSettingResponse {
    @Schema(description = "Unique identifier of the workspace", 
            example = "ws-12345")
    private String id;
    
    @Schema(description = "Name of the workspace", 
            example = "Marketing Team Workspace")
    private String name;
    
    @Schema(description = "Description of the workspace", 
            example = "Workspace for the marketing team's projects and collaborations")
    private String description;
    
    @Schema(description = "URL of the currently active logo based on portal preview settings", 
            example = "https://storage.example.com/logos/marketing-logo.png")
    private String logoUrl;
    
    @Schema(description = "URL of the primary logo", 
            example = "https://storage.example.com/logos/primary-logo.png")
    private String primaryLogoUrl;
    
    @Schema(description = "URL of the secondary logo", 
            example = "https://storage.example.com/logos/secondary-logo.png")
    private String secondaryLogoUrl;
    
    @Schema(description = "Logo type to be displayed in portal preview", 
            example = "PRIMARY", 
            allowableValues = {"PRIMARY", "SECONDARY"})
    private LogoType portalPreviewLogoType = LogoType.PRIMARY;
    
    @Schema(description = "Logo type to be displayed in shortlist preview", 
            example = "PRIMARY", 
            allowableValues = {"PRIMARY", "SECONDARY"})
    private LogoType shortlistPreviewLogoType = LogoType.PRIMARY;
    
    @Schema(description = "ID of the workspace owner", 
            example = "user-67890")
    private String ownerId;
    
    @Schema(description = "Timestamp when the workspace was created", 
            example = "2025-03-01T14:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "Timestamp when the workspace was last updated", 
            example = "2025-03-15T09:45:00")
    private LocalDateTime updatedAt;

    public static WorkspaceSettingResponse from(Workspace workspace) {
        WorkspaceSettingResponse response = new WorkspaceSettingResponse();
        response.setId(workspace.getId());
        response.setName(workspace.getName());
        response.setDescription(workspace.getDescription());
        response.setLogoUrl(workspace.getPortalPreviewLogoType() == LogoType.PRIMARY ? workspace.getLogoUrl() : workspace.getSecondaryLogoUrl());
        response.setPrimaryLogoUrl(workspace.getLogoUrl());
        response.setSecondaryLogoUrl(workspace.getSecondaryLogoUrl());
        response.setPortalPreviewLogoType(workspace.getPortalPreviewLogoType());
        response.setShortlistPreviewLogoType(workspace.getShortlistPreviewLogoType());
        response.setOwnerId(workspace.getOwnerId());
        response.setCreatedAt(workspace.getCreatedAt());
        response.setUpdatedAt(workspace.getUpdatedAt());
        return response;
    }
}
