package com.ginkgooai.core.workspace.repository;

import com.ginkgooai.core.workspace.domain.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, String> {
    List<Workspace> findByOwnerId(String ownerId);
    
    Optional<Workspace> findByIdAndOwnerId(String id, String ownerId);
    
    Optional<Workspace> findByName(String name);
    
    @Query("SELECT w FROM Workspace w WHERE w.ownerId = :ownerId AND w.status = 'ACTIVE'")
    List<Workspace> findActiveWorkspacesByOwnerId(String ownerId);
    
    boolean existsByNameAndOwnerId(String name, String ownerId);
}
