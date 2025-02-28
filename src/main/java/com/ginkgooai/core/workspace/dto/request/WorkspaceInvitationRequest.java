package com.ginkgooai.core.workspace.dto.request;

import com.ginkgooai.core.workspace.domain.WorkspaceRole;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WorkspaceInvitationRequest {
    @NotEmpty
    private String email;
    
    @NotNull
    private WorkspaceRole role;
}