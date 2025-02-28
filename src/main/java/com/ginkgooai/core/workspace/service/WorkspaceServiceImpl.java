package com.ginkgooai.core.workspace.service;

import com.ginkgooai.core.common.exception.ResourceDuplicatedException;
import com.ginkgooai.core.common.exception.ResourceNotFoundException;
import com.ginkgooai.core.workspace.client.identity.UserClient;
import com.ginkgooai.core.workspace.client.identity.dto.UserInfo;
import com.ginkgooai.core.workspace.client.messaging.EmailClient;
import com.ginkgooai.core.workspace.domain.*;
import com.ginkgooai.core.workspace.dto.request.WorkspaceCreateRequest;
import com.ginkgooai.core.workspace.dto.response.WorkspaceDetailResponse;
import com.ginkgooai.core.workspace.dto.request.WorkspaceInvitationRequest;
import com.ginkgooai.core.workspace.dto.request.WorkspaceUpdateRequest;
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
    private final EmailClient emailClient;


    @Transactional
    public Workspace createWorkspace(WorkspaceCreateRequest request, String userId) {
//        // 验证用户是否存在
//        if (!userClient.validateUser(userId)) {
//            throw new ResourceNotFoundException("Use", "userId", userId);
//        }

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

    public WorkspaceDetailResponse getWorkspaceByName(String name) {
        Workspace workspace = workspaceRepository.findById(name)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "name", name));
        
        UserInfo owner = userClient.getUserById(workspace.getOwnerId());
        
        return WorkspaceDetailResponse.from(workspace);
    }

    public List<WorkspaceDetailResponse> getWorkspacesByOwner(String userId) {
        List<Workspace> workspaces = workspaceRepository.findActiveWorkspacesByOwnerId(userId);
        UserInfo owner = userClient.getUserById(userId);
        
        return workspaces.stream()
                .map(workspace -> WorkspaceDetailResponse.from(workspace))
                .toList();
    }

    @Transactional
    public Workspace updateWorkspace(String id, WorkspaceUpdateRequest request, String userId) {
        Workspace workspace = workspaceRepository.findByIdAndOwnerId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "userId", userId));

        workspace.setName(request.getName());
        workspace.setDescription(request.getDescription());
        workspace.setLogoUrl(request.getLogoUrl());
        
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
    public WorkspaceInvitation inviteMember(String workspaceId, WorkspaceInvitationRequest request, String inviterId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "id", workspaceId));

        // Verify inviter has permission
        if (!workspace.getOwnerId().equals(inviterId) &&
                !memberRepository.findByWorkspaceIdAndUserIdAndRole(workspaceId, inviterId, WorkspaceRole.ADMIN).isPresent()) {
//            throw new AccessDeniedException("No permission to invite members");
            throw new RuntimeException("No permission to invite members");
        }

        // Check if user is already a member
        if (memberRepository.existsByWorkspaceIdAndUserId(workspaceId, request.getEmail())) {
            throw new ResourceDuplicatedException("Member", "email", request.getEmail());
        }

        // Check if there's already a pending invitation
        if (invitationRepository.existsByWorkspaceIdAndEmailAndStatus(
                workspaceId, request.getEmail(), WorkspaceInvitationStatus.PENDING)) {
            throw new ResourceDuplicatedException("Invitation", "email", request.getEmail());
        }

        WorkspaceInvitation invitation = new WorkspaceInvitation();
        invitation.setWorkspace(workspace);
        invitation.setEmail(request.getEmail());
        invitation.setRole(request.getRole());
        invitation.setInvitedBy(inviterId);
        invitation.setExpiresAt(LocalDateTime.now().plusDays(7));

        invitation = invitationRepository.save(invitation);

        // Send invitation email
        sendInvitationEmail(invitation);

        return invitation;
    }

    @Transactional
    public WorkspaceMember acceptInvitation(String invitationId, String userId) {
        WorkspaceInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation", "id", invitationId));

        // Verify invitation
        if (invitation.getStatus() != WorkspaceInvitationStatus.PENDING) {
//            throw new InvalidOperationException("Invitation is no longer valid");
            throw new RuntimeException("Invitation is no longer valid");
        }
        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            invitation.setStatus(WorkspaceInvitationStatus.EXPIRED);
            invitationRepository.save(invitation);
//            throw new InvalidOperationException("Invitation has expired");
            throw new RuntimeException("Invitation has expired");
        }

        // Verify user email matches invitation
        UserInfo user = userClient.getUserById(userId);
        if (!user.getEmail().equals(invitation.getEmail())) {
//            throw new AccessDeniedException("Email mismatch");
            throw new RuntimeException("Email mismatch");
        }

        // Create workspace member
        WorkspaceMember member = new WorkspaceMember();
        member.setWorkspace(invitation.getWorkspace());
        member.setUserId(userId);
        member.setRole(invitation.getRole());

        // Update invitation status
        invitation.setStatus(WorkspaceInvitationStatus.ACCEPTED);
        invitation.setAcceptedAt(LocalDateTime.now());
        invitationRepository.save(invitation);

        return memberRepository.save(member);
    }

    @Transactional
    public void rejectInvitation(String invitationId, String userId) {
        WorkspaceInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation", "id", invitationId));

        // Verify user email matches invitation
        UserInfo user = userClient.getUserById(userId);
        if (!user.getEmail().equals(invitation.getEmail())) {
//            throw new AccessDeniedException("Email mismatch");
            throw new RuntimeException("Email mismatch");
        }

        invitation.setStatus(WorkspaceInvitationStatus.REJECTED);
        invitationRepository.save(invitation);
    }

    @Transactional
    public void removeMember(String workspaceId, String memberId, String requesterId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "id", workspaceId));

        // Cannot remove workspace owner
        if (workspace.getOwnerId().equals(memberId)) {
//            throw new InvalidOperationException("Cannot remove workspace owner");
            throw new RuntimeException("Cannot remove workspace owner");
        }

        // Verify requester has permission
        if (!workspace.getOwnerId().equals(requesterId) &&
                !memberRepository.findByWorkspaceIdAndUserIdAndRole(workspaceId, requesterId, WorkspaceRole.ADMIN).isPresent()) {
//            throw new AccessDeniedException("No permission to remove members");
            throw new RuntimeException("No permission to remove members");
        }

        memberRepository.deleteByWorkspaceIdAndUserId(workspaceId, memberId);
    }

    private void sendInvitationEmail(WorkspaceInvitation invitation) {
        UserInfo inviter = userClient.getUserById(invitation.getInvitedBy());
        String invitationLink = generateInvitationLink(invitation.getId());

        String emailContent = String.format(
                "%s has invited you to join workspace %s. Click here to accept: %s",
                invitation.getInvitedBy(),
                invitation.getWorkspace().getName(),
                invitationLink
        );

        //TODO: Implement email client
        emailClient.sendEmail();
    }

    private String generateInvitationLink(String invitationId) {
        // Implementation depends on your frontend setup
        return "https://your-app.com/invitations/" + invitationId;
    }


    @Transactional
    public void updateMemberLastAccess(String workspaceId, String userId) {
        WorkspaceMember member = memberRepository.findByWorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkspaceMember", "workspaceId-userId",
                        String.join("-", workspaceId, userId)));

        member.setLastAccessedAt(LocalDateTime.now());
        memberRepository.save(member);
    }
}
