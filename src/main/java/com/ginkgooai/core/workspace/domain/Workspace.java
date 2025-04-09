package com.ginkgooai.core.workspace.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "workspace")
public class Workspace extends BaseAuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    private String description;

    private String domain;

    private String logoUrl;

    private String secondaryLogoUrl;

    @Enumerated(EnumType.STRING)
    private LogoType portalPreviewLogoType = LogoType.PRIMARY;
    
    @Enumerated(EnumType.STRING)
    private LogoType shortlistPreviewLogoType = LogoType.PRIMARY;

    @Enumerated(EnumType.STRING)
    private WorkspaceStatus status = WorkspaceStatus.ACTIVE;
}
