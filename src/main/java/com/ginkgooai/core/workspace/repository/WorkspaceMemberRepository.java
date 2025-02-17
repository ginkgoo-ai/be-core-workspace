package com.ginkgooai.core.workspace.repository;

import com.ginkgooai.core.workspace.domain.WorkspaceMember;
import com.ginkgooai.core.workspace.domain.WorkspaceRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, String> {
    boolean existsByWorkspaceIdAndUserId(String workspaceId, String userId);
    Optional<WorkspaceMember> findByWorkspaceIdAndUserId(String workspaceId, String userId);
    Optional<WorkspaceMember> findByWorkspaceIdAndUserIdAndRole(String workspaceId, String userId, WorkspaceRole role);
    void deleteByWorkspaceIdAndUserId(String workspaceId, String userId);
}