package com.ginkgooai.core.workspace.dto;

import com.ginkgooai.core.workspace.domain.Workspace;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WorkspaceDetailResponse {
    private String id;
    private String name;
    private String description;
    private String logoUrl;
    private String ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static WorkspaceDetailResponse from(Workspace workspace) {
        WorkspaceDetailResponse response = new WorkspaceDetailResponse();
        response.setId(workspace.getId());
        response.setName(workspace.getName());
        response.setDescription(workspace.getDescription());
        response.setLogoUrl(workspace.getLogoUrl());
        response.setOwnerId(workspace.getOwnerId());
        response.setCreatedAt(workspace.getCreatedAt());
        response.setUpdatedAt(workspace.getUpdatedAt());
        return response;
    }
}
