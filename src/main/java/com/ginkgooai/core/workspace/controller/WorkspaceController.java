package com.ginkgooai.core.workspace.controller;

import com.ginkgooai.core.workspace.client.identity.UserClient;
import com.ginkgooai.core.workspace.client.identity.dto.UserInfo;
import com.ginkgooai.core.workspace.domain.Workspace;
import com.ginkgooai.core.workspace.dto.WorkspaceCreateRequest;
import com.ginkgooai.core.workspace.dto.WorkspaceDetailResponse;
import com.ginkgooai.core.workspace.dto.WorkspaceUpdateRequest;
import com.ginkgooai.core.workspace.service.WorkspaceServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;


@RestController
@RequestMapping("/workspaces")
@RequiredArgsConstructor
@Tag(name = "Workspace Management", description = "APIs for managing workspaces")
@SecurityRequirement(name = "bearerAuth")
public class WorkspaceController {
    
    @Autowired
    UserClient userClient;

    private final WorkspaceServiceImpl workspaceService;

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
            @Valid @RequestBody WorkspaceCreateRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Workspace workspace = workspaceService.createWorkspace(request, jwt.getSubject());
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
    public WorkspaceDetailResponse getWorkspace(@AuthenticationPrincipal Jwt jwt, @PathVariable String name) {
        UserInfo userInfo = userClient.getUserById(jwt.getSubject());
        
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
    public List<WorkspaceDetailResponse> getWorkspaces(@AuthenticationPrincipal Jwt jwt) {
        return workspaceService.getWorkspacesByOwner(jwt.getSubject());
    }

    @PutMapping("/{id}")
    public Workspace updateWorkspace(
            @PathVariable String id,
            @Valid @RequestBody WorkspaceUpdateRequest request,
            @AuthenticationPrincipal UserInfo currentUser) {
        return workspaceService.updateWorkspace(id, request, currentUser.getId());
    }

    @DeleteMapping("/{id}")
    public void deleteWorkspace(
            @PathVariable String id,
            @AuthenticationPrincipal UserInfo currentUser) {
        workspaceService.deleteWorkspace(id, currentUser.getId());
    }
}
