package com.ginkgooai.core.workspace.controller;

import com.ginkgooai.core.workspace.service.WorkspaceServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workspaces/{workspaceId}/members")
@RequiredArgsConstructor
@Tag(name = "Workspace Members", description = "APIs for managing workspace members")
public class WorkspaceMemberController {
    
    private final WorkspaceServiceImpl workspaceService;
    
    private final HttpSession httpSession;

    @Operation(
            summary = "Update member's last access time",
            description = "Updates the last access time for a specific member in the workspace"
    )
    @ApiResponse(
            responseCode = "204",
            description = "Last access time updated successfully"
    )
    @ApiResponse(
            responseCode = "403",
            description = "Forbidden - No permission to access this workspace"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Not Found - Workspace or user does not exist"
    )
    @PatchMapping("/access")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateLastAccess(@Parameter(description = "Workspace ID")
                                 @PathVariable String workspaceId,
                                 @AuthenticationPrincipal Jwt jwt) {

        workspaceService.updateMemberLastAccess(workspaceId, jwt.getSubject());

        httpSession.setAttribute("CURRENT_WORKSPACE_ID", workspaceId);
    }
}
