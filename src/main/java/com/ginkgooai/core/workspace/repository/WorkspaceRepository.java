package com.ginkgooai.core.workspace.repository;

import com.ginkgooai.core.workspace.domain.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceRepository
    extends JpaRepository<Workspace, String>, JpaSpecificationExecutor<Workspace> {
    List<Workspace> findByCreatedBy(String ownerId);

    Optional<Workspace> findByIdAndCreatedBy(String id, String ownerId);

    @Query("SELECT w FROM Workspace w WHERE w.createdBy = :ownerId AND w.status = 'ACTIVE'")
    List<Workspace> findActiveWorkspacesByOwnerId(String ownerId);

    boolean existsByNameAndCreatedBy(String name, String ownerId);

    Optional<Workspace> findByDomain(String domain);

    boolean existsByDomain(String domain);

    Optional<Workspace> findById(String id);

    List<Workspace> findByIdIn(List<String> ids);

    boolean existsByName(String name);
}
