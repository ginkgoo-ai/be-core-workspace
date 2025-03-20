package com.ginkgooai.core.workspace.controller;

import com.ginkgooai.core.common.utils.ContextUtils;
import com.ginkgooai.core.workspace.client.identity.UserClient;
import com.ginkgooai.core.workspace.client.identity.dto.UserInfo;
import com.ginkgooai.core.workspace.domain.Workspace;
import com.ginkgooai.core.workspace.dto.request.WorkspaceCreateRequest;
import com.ginkgooai.core.workspace.dto.request.WorkspaceUpdateRequest;
import com.ginkgooai.core.workspace.dto.response.WorkspaceDetailResponse;
import com.ginkgooai.core.workspace.service.WorkspaceContextService;
import com.ginkgooai.core.workspace.service.WorkspaceServiceImpl;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/workspaces")
@RequiredArgsConstructor
@Tag(name = "Workspace Management", description = "APIs for managing workspaces")
@SecurityRequirement(name = "bearerAuth")
public class WorkspaceController {

    private final WorkspaceServiceImpl workspaceService;
    private final WorkspaceContextService workspaceContextService;

    @PostMapping
    @Operation(
            summary = "Create a new workspace",
            description = "Creates a new workspace for the authenticated user"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Workspace created successfully",
            content = @Content(schema = @Schema(implementation = WorkspaceDetailResponse.class))
    )
    public WorkspaceDetailResponse createWorkspace(
            @Valid @RequestBody WorkspaceCreateRequest request) {
        Workspace workspace = workspaceService.createWorkspace(request, ContextUtils.getUserId());
        return WorkspaceDetailResponse.from(workspace);
    }

    @GetMapping("/{name}")
    @Operation(
            summary = "Get workspace by name",
            description = "Retrieves a workspace by its name"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Workspace found",
            content = @Content(schema = @Schema(implementation = WorkspaceDetailResponse.class))
    )
    @Hidden
    public WorkspaceDetailResponse getWorkspace(@PathVariable String name) {
        return workspaceService.getWorkspaceByName(name);
    }

    @GetMapping
    @Operation(
            summary = "Get all workspaces",
            description = "Retrieves all workspaces owned by the authenticated user"
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of workspaces",
            content = @Content(schema = @Schema(implementation = WorkspaceDetailResponse.class))
    )
    public List<WorkspaceDetailResponse> getWorkspaces(HttpServletResponse response) {
        List<WorkspaceDetailResponse> workspaces = workspaceService.getWorkspacesByOwner(ContextUtils.getUserId());

        if (!ObjectUtils.isEmpty(workspaces.size())) {
            workspaceContextService.setUserWorkspaceContext(ContextUtils.getUserId(), workspaces.stream().map(WorkspaceDetailResponse::getId).toList());
        }

        return workspaces;
    }

    @PutMapping("/{id}")
    @Hidden
    public Workspace updateWorkspace(@PathVariable String id,
                                     @Valid @RequestBody WorkspaceUpdateRequest request) {
        return workspaceService.updateWorkspace(id, request, ContextUtils.getUserId());
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Partially update workspace",
            description = "Updates specific fields of an existing workspace"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Workspace updated successfully",
            content = @Content(schema = @Schema(implementation = WorkspaceDetailResponse.class))
    )
    public WorkspaceDetailResponse partialUpdateWorkspace(
            @PathVariable String id,
            @Valid @RequestBody WorkspaceUpdateRequest request) {
        Workspace workspace = workspaceService.partialUpdateWorkspace(id, request, ContextUtils.getUserId());
        return WorkspaceDetailResponse.from(workspace);
    }
    @DeleteMapping("/{id}")
    public void deleteWorkspace(@PathVariable String id) {
        workspaceService.deleteWorkspace(id, ContextUtils.getUserId());
    }
    
    @GetMapping("/{id}/validate")
    @Hidden
    public boolean validate(@PathVariable String id) {
        return workspaceContextService.validateUserWorkspaceAccess(ContextUtils.getUserId(), id);

    }
}
