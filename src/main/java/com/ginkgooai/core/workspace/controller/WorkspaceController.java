package com.ginkgooai.core.workspace.controller;

import com.ginkgooai.core.workspace.client.identity.UserClient;
import com.ginkgooai.core.workspace.client.identity.dto.UserInfo;
import com.ginkgooai.core.workspace.domain.Workspace;
import com.ginkgooai.core.workspace.dto.WorkspaceCreateRequest;
import com.ginkgooai.core.workspace.dto.WorkspaceDetailResponse;
import com.ginkgooai.core.workspace.dto.WorkspaceUpdateRequest;
import com.ginkgooai.core.workspace.service.WorkspaceServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {
    
    @Autowired
    UserClient userClient;

    private final WorkspaceServiceImpl workspaceService;

    @PostMapping
    public WorkspaceDetailResponse createWorkspace(
            @Valid @RequestBody WorkspaceCreateRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Workspace workspace = workspaceService.createWorkspace(request, jwt.getSubject());
        return WorkspaceDetailResponse.from(workspace);
    }

    @GetMapping("/{name}")
    public WorkspaceDetailResponse getWorkspace(@AuthenticationPrincipal Jwt jwt, @PathVariable String name) {
        UserInfo userInfo = userClient.getUserById(jwt.getSubject());
        
        return workspaceService.getWorkspaceByName(name);
    }

    @GetMapping
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
