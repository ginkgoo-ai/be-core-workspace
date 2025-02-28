package com.ginkgooai.core.workspace.service;

import com.ginkgooai.core.common.constant.RedisKey;
import com.ginkgooai.core.common.exception.ResourceNotFoundException;
import com.ginkgooai.core.workspace.domain.Workspace;
import com.ginkgooai.core.workspace.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkspaceContextService {
    private final RedisTemplate<String, String> redisTemplate;
    private final WorkspaceRepository workspaceRepository;
    
    private static final long EXPIRATION = 24 * 60 * 60; // 24 hours
    private static final long REFRESH_THRESHOLD = 6 * 60 * 60; // 6 hours, for early refresh
    
    /**
     * Sets the workspace context for a user
     * @param userId The user identifier
     * @param workspaceId The workspace identifier
     */
    public void setUserWorkspaceContext(String userId, String workspaceId) {
        String key = RedisKey.WORKSPACE_CONTEXT_KEY_PREFIX + userId;
        redisTemplate.opsForValue().set(key, workspaceId, EXPIRATION, TimeUnit.SECONDS);
        log.debug("Set workspace context for user {}: {}", userId, workspaceId);
    }
    
    /**
     * Gets the workspace context for a user
     * If not found or about to expire, automatically refreshes it
     * @param userId The user identifier
     * @return The workspace identifier
     */
    public String getUserWorkspaceContext(String userId) {
        String key = RedisKey.WORKSPACE_CONTEXT_KEY_PREFIX  + userId;
        String workspaceId = redisTemplate.opsForValue().get(key);
        
        // If workspace context doesn't exist, try to set it automatically
        if (workspaceId == null) {
            workspaceId = autoSetUserWorkspaceContext(userId);
        } else {
            // Check if approaching expiration time, if so refresh it
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            if (ttl != null && ttl > 0 && ttl < REFRESH_THRESHOLD) {
                log.debug("Refreshing expiring workspace context for user {}", userId);
                redisTemplate.expire(key, EXPIRATION, TimeUnit.SECONDS);
            }
        }
        
        return workspaceId;
    }
    
    /**
     * Automatically sets workspace context for a user
     * - If user has only one workspace, selects it automatically
     * - If user has multiple workspaces, tries to select the last used one
     * @param userId The user identifier
     * @return The selected workspace identifier or null if none available
     */
    private String autoSetUserWorkspaceContext(String userId) {
        // Get all workspaces for the user
        List<Workspace> userWorkspaceIds = workspaceRepository.findByOwnerId(userId);
        
        if (userWorkspaceIds.isEmpty()) {
            log.warn("User {} has no workspaces", userId);
            throw new ResourceNotFoundException("Workspace", "userId", userId);
        }
        
        // User has only one workspace, use it directly
        String workspaceId = userWorkspaceIds.get(0).getId();
        log.info("Auto-selecting the only workspace {} for user {}", workspaceId, userId);
        setUserWorkspaceContext(userId, workspaceId);
        return workspaceId;
    }
}
