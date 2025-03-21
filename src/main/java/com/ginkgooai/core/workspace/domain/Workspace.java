package com.ginkgooai.core.workspace.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "workspace")
public class Workspace {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "owner_id", length = 36, nullable = false)
    private String ownerId;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @Column(name = "secondary_logo_url", length = 255)
    private String secondaryLogoUrl;

    @Column(name = "portal_preview_logo_type", length = 20)
    @Enumerated(EnumType.STRING)
    private LogoType portalPreviewLogoType = LogoType.PRIMARY;
    
    @Column(name = "shortlist_preview_logo_type", length = 20)
    @Enumerated(EnumType.STRING)
    private LogoType shortlistPreviewLogoType = LogoType.PRIMARY;

    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkspaceStatus status = WorkspaceStatus.ACTIVE;

    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WorkspaceMember> members = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

}
