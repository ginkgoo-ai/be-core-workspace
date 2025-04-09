package com.ginkgooai.core.workspace.service;

import com.ginkgooai.core.common.exception.ResourceDuplicatedException;
import com.ginkgooai.core.common.exception.ResourceNotFoundException;
import com.ginkgooai.core.common.utils.ContextUtils;
import com.ginkgooai.core.workspace.domain.Workspace;
import com.ginkgooai.core.workspace.domain.WorkspaceStatus;
import com.ginkgooai.core.workspace.dto.request.WorkspaceCreateRequest;
import com.ginkgooai.core.workspace.dto.request.WorkspacePatchRequest;
import com.ginkgooai.core.workspace.dto.request.WorkspaceUpdateRequest;
import com.ginkgooai.core.workspace.dto.response.WorkspaceSettingResponse;
import com.ginkgooai.core.workspace.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl {
    
    private final WorkspaceRepository workspaceRepository;

    @Transactional
    public Workspace createWorkspace(WorkspaceCreateRequest request, String userId) {
        if (workspaceRepository.existsByNameAndCreatedBy(request.getName(), userId)) {
            throw new ResourceDuplicatedException("Workspace", "name-userid", String.join("-", request.getName(), userId));
        }

        if (workspaceRepository.existsByDomain(request.getDomain())) {
            throw new ResourceDuplicatedException("Workspace", "domain", request.getDomain());
        }

        Workspace workspace = new Workspace();
        workspace.setName(request.getName());
        workspace.setDescription(request.getDescription());
        workspace.setLogoUrl(request.getLogoUrl());

        return workspaceRepository.save(workspace);
    }

    public WorkspaceSettingResponse getWorkspaceByDomain(String domain) {
        Workspace workspace = workspaceRepository.findByDomain(domain)
            .orElseThrow(() -> new ResourceNotFoundException("Workspace", "domain", domain));
        return WorkspaceSettingResponse.from(workspace);
    }

    public Workspace getCurrentWorkspace() {
        Workspace workspace = workspaceRepository.findById(ContextUtils.getWorkspaceId())
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "workspaceId", ContextUtils.getWorkspaceId()));

        return workspace;
    }

    public List<Workspace> getWorkspacesByOwner(String userId) {
        return workspaceRepository.findActiveWorkspacesByOwnerId(userId);
    }

    @Transactional
    public Workspace updateWorkspace(String id, WorkspaceUpdateRequest request, String userId) {
        Workspace workspace = workspaceRepository.findByIdAndCreatedBy(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "userId", userId));

        if (!workspace.getName().equals(request.getName()) &&
            workspaceRepository.existsByNameAndCreatedBy(request.getName(), userId)) {
            throw new ResourceDuplicatedException("Workspace", "name-userid", String.join("-", request.getName(), userId));
        }
        workspace.setName(request.getName());
        workspace.setDescription(request.getDescription());
        workspace.setLogoUrl(request.getLogoUrl());
        workspace.setSecondaryLogoUrl(request.getSecondaryLogoUrl());
        workspace.setPortalPreviewLogoType(request.getPortalPreviewLogoType());
        workspace.setShortlistPreviewLogoType(request.getShortlistPreviewLogoType());

        return workspaceRepository.save(workspace);
    }

    @Transactional
    public void deleteWorkspace(String id, String userId) {
        Workspace workspace = workspaceRepository.findByIdAndCreatedBy(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "id-userId", String.join("-", id, userId)));
        
        workspace.setStatus(WorkspaceStatus.DELETED);
        workspaceRepository.save(workspace);
    }

    @Transactional
    public Workspace partialUpdateWorkspace(String id, WorkspacePatchRequest request, String userId) {
        Workspace workspace = workspaceRepository.findByIdAndCreatedBy(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "userId", userId));

        if (request.getName() != null) {
            if (!workspace.getName().equals(request.getName()) &&
                workspaceRepository.existsByNameAndCreatedBy(request.getName(), userId)) {
                throw new ResourceDuplicatedException("Workspace", "name-userid", String.join("-", request.getName(), userId));
            }
            workspace.setName(request.getName());
        }

//       //not supported yet 
//        if (request.getDomain() != null) {
//            if (!workspace.getDomain().equals(request.getDomain()) &&
//                workspaceRepository.existsByDomain(request.getDomain())) {
//                throw new ResourceDuplicatedException("Workspace", "domain", request.getDomain());
//            }
//            workspace.setDomain(request.getDomain());
//        }
        
        if (request.getDescription() != null) {
            workspace.setDescription(request.getDescription());
        }
        
        if (request.getLogoUrl() != null) {
            workspace.setLogoUrl(request.getLogoUrl());
        }
        
        if (request.getSecondaryLogoUrl() != null) {
            workspace.setSecondaryLogoUrl(request.getSecondaryLogoUrl());
        }
        
        if (request.getPortalPreviewLogoType() != null) {
            workspace.setPortalPreviewLogoType(request.getPortalPreviewLogoType());
        }
        
        if (request.getShortlistPreviewLogoType() != null) {
            workspace.setShortlistPreviewLogoType(request.getShortlistPreviewLogoType());
        }

        return workspaceRepository.save(workspace);
    }
}
