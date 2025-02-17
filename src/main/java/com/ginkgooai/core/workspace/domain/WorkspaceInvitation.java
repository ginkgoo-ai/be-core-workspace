package com.ginkgooai.core.workspace.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "workspace_invitation")
@Getter
@Setter
public class WorkspaceInvitation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkspaceRole role;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkspaceInvitationStatus status = WorkspaceInvitationStatus.PENDING;

    @Column(name = "invited_by", nullable = false)
    private String invitedBy;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;
}