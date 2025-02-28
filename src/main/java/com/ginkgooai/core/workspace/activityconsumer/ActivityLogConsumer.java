package com.ginkgooai.core.workspace.activityconsumer;

import com.ginkgooai.core.common.bean.ActivityType;
import com.ginkgooai.core.common.message.ActivityLogMessage;
import com.ginkgooai.core.workspace.domain.ActivityLog;
import com.ginkgooai.core.workspace.service.ActivityLogService;
import com.ginkgooai.core.workspace.service.WorkspaceContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * Consumer for processing activity log messages from Redis queue
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ActivityLogConsumer {

    private static final String ACTIVITY_LOG_QUEUE = "activity_log_queue";
    
    private final RedissonClient redissonClient;
    private final ActivityLogService activityLogService;
    private final WorkspaceContextService workspaceContextService;


    @Value("${activity.log.consumer.batch-size:100}")
    private int batchSize;
    
    @Value("${activity.log.consumer.max-wait-time:5000}")
    private long maxWaitTimeMs;

    /**
     * Scheduled method to consume messages from the activity log queue
     * Uses batch processing for better performance
     * Implements circuit breaker pattern to handle downstream service failures
     */
    @Scheduled(fixedDelayString = "${activity.log.consumer.polling-interval:1000}")
    public void consumeActivityLogs() {
        RQueue<ActivityLogMessage> queue = redissonClient.getQueue(ACTIVITY_LOG_QUEUE);
        try {
            // Poll for messages with timeout to avoid empty loops
            log.trace("Running scheduled task on thread: " + Thread.currentThread().getName());
            List<ActivityLogMessage> messages = queue.poll(100);
            if (!CollectionUtils.isEmpty(messages)) {
                processBatch(messages);
            }
        } catch (Exception e) {
            log.error("Error consuming activity logs", e);
        }
    }
    
    /**
     * Process a batch of activity log messages
     * Delegates to service layer for actual persistence
     * 
     * @param batch List of messages to process
     */
    private void processBatch(List<ActivityLogMessage> batch) {
        try {
            log.debug("Processing batch of {} activity logs", batch.size());
            activityLogService.createLogs(batch.stream().map(this::convertToEntity).toList());
            log.debug("Successfully processed batch of activity logs");
        } catch (Exception e) {
            log.error("Failed to process batch of activity logs", e);
        }
    }
    
    private ActivityLog convertToEntity(ActivityLogMessage message) {
        String workspaceId = ObjectUtils.isEmpty(message.getWorkspaceId()) ? 
            workspaceContextService.getUserWorkspaceContext(message.getCreatedBy()) : message.getWorkspaceId();
        return ActivityLog.builder()
                .workspaceId(workspaceId)
                .projectId(message.getProjectId())
                .applicationId(message.getApplicationId())
                .activityType(ActivityType.valueOf(message.getActivityType()))
                .description(message.getDescription())
                .createdBy(message.getCreatedBy())
                .createdAt(message.getCreatedAt())
                .context(message.getContext()) //to jsonString
                .build();
    }
}
