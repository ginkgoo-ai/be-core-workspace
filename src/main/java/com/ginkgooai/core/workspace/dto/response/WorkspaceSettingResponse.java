package com.ginkgooai.core.workspace.dto.response;

import com.ginkgooai.core.workspace.domain.LogoType;
import com.ginkgooai.core.workspace.domain.Workspace;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WorkspaceSettingResponse {
    private String id;
    private String name;
    private String description;
    private String logoUrl;
    private String secondaryLogoUrl;
    private LogoType portalPreviewLogoType = LogoType.PRIMARY;
    private LogoType shortlistPreviewLogoType = LogoType.PRIMARY;
    private String ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static WorkspaceSettingResponse from(Workspace workspace) {
        WorkspaceSettingResponse response = new WorkspaceSettingResponse();
        response.setId(workspace.getId());
        response.setName(workspace.getName());
        response.setDescription(workspace.getDescription());
        response.setLogoUrl(workspace.getLogoUrl());
        response.setSecondaryLogoUrl(workspace.getSecondaryLogoUrl());
        response.setPortalPreviewLogoType(workspace.getPortalPreviewLogoType());
        response.setShortlistPreviewLogoType(workspace.getShortlistPreviewLogoType());
        response.setOwnerId(workspace.getOwnerId());
        response.setCreatedAt(workspace.getCreatedAt());
        response.setUpdatedAt(workspace.getUpdatedAt());
        return response;
    }
}
