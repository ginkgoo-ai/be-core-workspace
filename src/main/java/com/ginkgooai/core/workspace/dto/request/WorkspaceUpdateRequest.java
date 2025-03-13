package com.ginkgooai.core.workspace.dto.request;

import com.ginkgooai.core.workspace.domain.LogoType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WorkspaceUpdateRequest {
    @NotBlank
    @Size(max = 100)
    private String name;
    
    @Size(max = 500)
    private String description;
   
    private String logoUrl;
    
    private String secondaryLogoUrl;
    
    private LogoType portalPreviewLogoType = LogoType.PRIMARY;
    
    private LogoType shortlistPreviewLogoType = LogoType.PRIMARY;
    
}