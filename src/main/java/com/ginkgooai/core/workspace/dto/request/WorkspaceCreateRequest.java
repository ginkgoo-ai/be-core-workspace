package com.ginkgooai.core.workspace.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WorkspaceCreateRequest {
    @Schema(description = "Name of the workspace", example = "My Workspace", required = true)
    @Pattern(regexp = "^[a-zA-Z0-9\\s\\-_&@.]{3,50}$",
        message = "Name must be 3-50 characters long and can only contain letters, numbers, spaces and common symbols (-_&@.)")
    private String name;

    @Schema(description = "Domain identifier for the workspace (must be unique)",
        example = "marketing-team")
    @Pattern(regexp = "^[a-z0-9-]{3,50}$",
        message = "Domain must be 3-50 characters long and contain only lowercase letters, numbers, and hyphens")
    private String domain;

    @Size(max = 500)
    @Schema(description = "Description of the workspace", example = "This is my team's workspace for project collaboration")
    private String description;

    @Schema(description = "URL of the primary logo", example = "https://example.com/logos/primary.png")
    private String logoUrl;

}