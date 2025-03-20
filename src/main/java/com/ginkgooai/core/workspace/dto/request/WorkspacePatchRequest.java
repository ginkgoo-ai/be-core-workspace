package com.ginkgooai.core.workspace.dto.request;

import com.ginkgooai.core.workspace.domain.LogoType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request object for patch updating workspace details")
public class WorkspacePatchRequest {
    @Size(max = 100)
    @Schema(description = "Name of the workspace", example = "My Workspace", required = true)
    private String name;
    
    @Size(max = 500)
    @Schema(description = "Description of the workspace", example = "This is my team's workspace for project collaboration")
    private String description;
   
    @Schema(description = "URL of the primary logo", example = "https://example.com/logos/primary.png")
    private String logoUrl;
    
    @Schema(description = "URL of the secondary logo", example = "https://example.com/logos/secondary.png")
    private String secondaryLogoUrl;
    
    @Schema(description = "Logo type to use in portal preview", example = "PRIMARY", allowableValues = {"PRIMARY", "SECONDARY"})
    private LogoType portalPreviewLogoType = LogoType.PRIMARY;
    
    @Schema(description = "Logo type to use in shortlist preview", example = "PRIMARY", allowableValues = {"PRIMARY", "SECONDARY"})
    private LogoType shortlistPreviewLogoType = LogoType.PRIMARY;
    
}