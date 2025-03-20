package com.ginkgooai.core.workspace.service;

import com.ginkgooai.core.common.exception.ResourceDuplicatedException;
import com.ginkgooai.core.common.exception.ResourceNotFoundException;
import com.ginkgooai.core.common.utils.ContextUtils;
import com.ginkgooai.core.workspace.client.identity.UserClient;
import com.ginkgooai.core.workspace.client.identity.dto.UserInfo;
import com.ginkgooai.core.workspace.domain.*;
import com.ginkgooai.core.workspace.dto.request.WorkspaceCreateRequest;
import com.ginkgooai.core.workspace.dto.request.WorkspacePatchRequest;
import com.ginkgooai.core.workspace.dto.response.WorkspaceSettingResponse;
import com.ginkgooai.core.workspace.dto.request.WorkspaceUpdateRequest;
import com.ginkgooai.core.workspace.dto.response.WorkspaceResponse;
import com.ginkgooai.core.workspace.repository.WorkspaceInvitationRepository;
import com.ginkgooai.core.workspace.repository.WorkspaceMemberRepository;
import com.ginkgooai.core.workspace.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl {
    
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository memberRepository;
    private final WorkspaceInvitationRepository invitationRepository;
    private final UserClient userClient;

    @Transactional
    public Workspace createWorkspace(WorkspaceCreateRequest request, String userId) {
        if (workspaceRepository.existsByNameAndOwnerId(request.getName(), userId)) {
            throw new ResourceDuplicatedException("Workspace", "name-userid", String.join("-", request.getName(), userId));
        }

        Workspace workspace = new Workspace();
        workspace.setName(request.getName());
        workspace.setDescription(request.getDescription());
        workspace.setOwnerId(userId);
        workspace.setLogoUrl(request.getLogoUrl());

        WorkspaceMember ownerMember = new WorkspaceMember();
        ownerMember.setWorkspace(workspace);
        ownerMember.setUserId(userId);
        ownerMember.setRole(WorkspaceRole.OWNER);
        
        workspace.getMembers().add(ownerMember);
        
        return workspaceRepository.save(workspace);
    }

    public WorkspaceSettingResponse getWorkspaceByName(String name) {
        Workspace workspace = workspaceRepository.findById(name)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "name", name));
        
        UserInfo owner = userClient.getUserById(workspace.getOwnerId()).getBody();
        
        return WorkspaceSettingResponse.from(workspace);
    }

    public Workspace getCurrentWorkspace() {
        Workspace workspace = workspaceRepository.findByIdAndOwnerId(ContextUtils.getWorkspaceId(), ContextUtils.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "workspaceId:userId", String.join(":", ContextUtils.getWorkspaceId(), ContextUtils.getUserId())));

        return workspace;
    }

    public List<Workspace> getWorkspacesByOwner(String userId) {
        return workspaceRepository.findActiveWorkspacesByOwnerId(userId);
    }

    @Transactional
    public Workspace updateWorkspace(String id, WorkspaceUpdateRequest request, String userId) {
        Workspace workspace = workspaceRepository.findByIdAndOwnerId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "userId", userId));

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
        Workspace workspace = workspaceRepository.findByIdAndOwnerId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "id-userId", String.join("-", id, userId)));
        
        workspace.setStatus(WorkspaceStatus.DELETED);
        workspaceRepository.save(workspace);
    }

    @Transactional
    public void updateMemberLastAccess(String workspaceId, String userId) {
        WorkspaceMember member = memberRepository.findByWorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkspaceMember", "workspaceId-userId",
                        String.join("-", workspaceId, userId)));

        member.setLastAccessedAt(LocalDateTime.now());
        memberRepository.save(member);
    }

    @Transactional
    public Workspace partialUpdateWorkspace(String id, WorkspacePatchRequest request, String userId) {
        Workspace workspace = workspaceRepository.findByIdAndOwnerId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "userId", userId));

        if (request.getName() != null) {
            workspace.setName(request.getName());
        }
        
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
