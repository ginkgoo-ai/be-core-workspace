package com.ginkgooai.core.workspace.controller;

import com.ginkgooai.core.common.utils.ContextUtils;
import com.ginkgooai.core.workspace.domain.Workspace;
import com.ginkgooai.core.workspace.dto.request.WorkspaceCreateRequest;
import com.ginkgooai.core.workspace.dto.request.WorkspacePatchRequest;
import com.ginkgooai.core.workspace.dto.request.WorkspaceUpdateRequest;
import com.ginkgooai.core.workspace.dto.response.WorkspaceResponse;
import com.ginkgooai.core.workspace.dto.response.WorkspaceSettingResponse;
import com.ginkgooai.core.workspace.service.WorkspaceContextService;
import com.ginkgooai.core.workspace.service.WorkspaceServiceImpl;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/workspaces")
@RequiredArgsConstructor
@Tag(name = "Workspace Management", description = "APIs for managing workspaces")
public class WorkspaceController {

	private final WorkspaceServiceImpl workspaceService;

	private final WorkspaceContextService workspaceContextService;

	@PostMapping
	@Operation(summary = "Create a new workspace", description = "Creates a new workspace for the authenticated user")
	@ApiResponse(responseCode = "200", description = "Workspace created successfully",
			content = @Content(schema = @Schema(implementation = WorkspaceSettingResponse.class)))
	public WorkspaceSettingResponse createWorkspace(@Valid @RequestBody WorkspaceCreateRequest request) {
		Workspace workspace = workspaceService.createWorkspace(request, ContextUtils.getUserId());
		return WorkspaceSettingResponse.from(workspace, request.getLogoUrl());
	}

	@GetMapping("/current")
	@Operation(summary = "Get current workspace details for setting",
			description = "Retrieves the setting details of currently logged in workspace")
	@ApiResponse(responseCode = "200", description = "Current workspace setting details retrieved successfully",
			content = @Content(schema = @Schema(implementation = WorkspaceSettingResponse.class)))
	@ApiResponse(responseCode = "404", description = "Current workspace not found")
	public WorkspaceSettingResponse getCurrentWorkspace() {
		return WorkspaceSettingResponse.from(workspaceService.getCurrentWorkspace());
	}

	@GetMapping
	@Operation(summary = "Get all workspaces", description = "Retrieves all workspaces owned by the authenticated user")
	@ApiResponse(responseCode = "200", description = "List of workspaces",
			content = @Content(schema = @Schema(implementation = WorkspaceResponse.class)))
	public List<WorkspaceResponse> getWorkspaces(HttpServletResponse response) {
		List<Workspace> workspaces = workspaceService.getWorkspacesByOwner(ContextUtils.getUserId());

		if (!ObjectUtils.isEmpty(workspaces.size())) {
			workspaceContextService.setUserWorkspaceContext(ContextUtils.getUserId(),
					workspaces.stream().map(Workspace::getId).toList());
		}

		return workspaces.stream().map(WorkspaceResponse::from).collect(Collectors.toList());
	}

	@PutMapping("/{id}")
	@Hidden
	public Workspace updateWorkspace(@PathVariable String id, @Valid @RequestBody WorkspaceUpdateRequest request) {
		return workspaceService.updateWorkspace(id, request, ContextUtils.getUserId());
	}

	@PatchMapping("/{id}")
	@Operation(summary = "Partially update workspace", description = "Updates specific fields of an existing workspace")
	@ApiResponse(responseCode = "200", description = "Workspace updated successfully",
			content = @Content(schema = @Schema(implementation = WorkspaceSettingResponse.class)))
	public WorkspaceSettingResponse partialUpdateWorkspace(@PathVariable String id,
			@Valid @RequestBody WorkspacePatchRequest request) {
		Workspace workspace = workspaceService.partialUpdateWorkspace(id, request, ContextUtils.getUserId());
		return WorkspaceSettingResponse.from(workspace, request.getLogoUrl());
	}

	@DeleteMapping("/{id}")
	public void deleteWorkspace(@PathVariable String id) {
		workspaceService.deleteWorkspace(id, ContextUtils.getUserId());
	}

	@GetMapping("/{id}/validate")
	@Hidden
	public boolean validate(@PathVariable String id) {
		return workspaceContextService.validateUserWorkspaceAccess(ContextUtils.getUserId(), id);

	}

	@GetMapping("/domains/{domain}/availability")
	@Operation(summary = "Check if a workspace domain is available",
			description = "Checks if a given domain name is available for a new workspace.")
	@ApiResponse(responseCode = "204", description = "Domain is available")
	@ApiResponse(responseCode = "409", description = "Domain is already taken")
	public ResponseEntity<Void> checkDomainAvailabilityRestful(
			@Parameter(description = "The domain name to check", required = true) @PathVariable String domain) {
		boolean available = workspaceService.checkDomainAvailability(domain);
		return available ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

}
