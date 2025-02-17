package com.ginkgooai.core.workspace.repository;

import com.ginkgooai.core.workspace.domain.WorkspaceInvitation;
import com.ginkgooai.core.workspace.domain.WorkspaceInvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkspaceInvitationRepository extends JpaRepository<WorkspaceInvitation, String> {
    boolean existsByWorkspaceIdAndEmailAndStatus(String workspaceId, String email, WorkspaceInvitationStatus status);
    List<WorkspaceInvitation> findByEmailAndStatus(String email, WorkspaceInvitationStatus status);
}

