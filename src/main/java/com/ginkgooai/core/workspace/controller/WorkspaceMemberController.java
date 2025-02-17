package com.ginkgooai.core.workspace.controller;

import com.ginkgooai.core.workspace.service.WorkspaceServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/members")
@RequiredArgsConstructor
public class WorkspaceMemberController {
    private final WorkspaceServiceImpl workspaceService;

    @PatchMapping("/{userId}/access")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateLastAccess(
        @PathVariable String workspaceId,
        @PathVariable String userId
    ) {
        workspaceService.updateMemberLastAccess(workspaceId, userId);
    }
}