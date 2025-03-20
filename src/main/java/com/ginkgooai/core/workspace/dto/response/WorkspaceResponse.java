package com.ginkgooai.core.workspace.dto.response;

import com.ginkgooai.core.workspace.domain.LogoType;
import com.ginkgooai.core.workspace.domain.Workspace;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WorkspaceResponse {
    private String id;
    private String name;
    private String description;
    private String logoUrl;
    private String ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static WorkspaceResponse from(Workspace workspace) {
        WorkspaceResponse response = new WorkspaceResponse();
        response.setId(workspace.getId());
        response.setName(workspace.getName());
        response.setDescription(workspace.getDescription());
        response.setLogoUrl(workspace.getPortalPreviewLogoType() == LogoType.PRIMARY ? workspace.getLogoUrl() : workspace.getSecondaryLogoUrl());
        response.setOwnerId(workspace.getOwnerId());
        response.setCreatedAt(workspace.getCreatedAt());
        response.setUpdatedAt(workspace.getUpdatedAt());
        return response;
    }
}
