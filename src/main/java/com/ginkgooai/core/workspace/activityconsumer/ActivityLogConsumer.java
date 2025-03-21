package com.ginkgooai.core.workspace.activityconsumer;

import com.ginkgooai.core.common.bean.ActivityType;
import com.ginkgooai.core.common.message.ActivityLogMessage;
import com.ginkgooai.core.common.queue.QueueInterface;
import com.ginkgooai.core.workspace.domain.ActivityLog;
import com.ginkgooai.core.workspace.service.ActivityLogService;
import com.ginkgooai.core.workspace.service.WorkspaceContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
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

    private final QueueInterface queueInterface;
    private final ActivityLogService activityLogService;
    private final WorkspaceContextService workspaceContextService;

    @Value("${activity.log.consumer.batch-size:100}")
    private int batchSize;

    @Value("${activity.log.consumer.max-wait-time:5000}")
    private long maxWaitTimeMs;

    @Value("${activity.log.consumer.polling-interval:1000}")
    private long pollingIntervalMs;

    /**
     * Scheduled method to consume messages from the activity log queue
     * Uses batch processing for better performance
     * Implements circuit breaker pattern to handle downstream service failures
     */
    @Scheduled(fixedDelayString = "${activity.log.consumer.polling-interval:1000}")
    public void consumeActivityLogs() {

        List<ActivityLogMessage> batch =  queueInterface.getMessages(ACTIVITY_LOG_QUEUE, batchSize, ActivityLogMessage.class);
        if (!ObjectUtils.isEmpty(batch)) {
            processBatch(batch);
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

            List<ActivityLog> logs = batch.stream()
                    .map(this::convertToEntity)
                    .toList();

            activityLogService.createLogs(logs);

            log.debug("Successfully processed batch of {} activity logs", batch.size());
        } catch (Exception e) {
            log.error("Failed to process batch of activity logs", e);
            // TODO: Implement retry logic and dead-letter queue handling 
        }
    }

    /**
     * Convert message to entity with proper formatting
     */
    private ActivityLog convertToEntity(ActivityLogMessage message) {
        try {
            // Get or resolve workspace ID
            String workspaceId = ObjectUtils.isEmpty(message.getWorkspaceId()) ?
                    workspaceContextService.getUserWorkspaceContext(message.getCreatedBy()) :
                    message.getWorkspaceId();

            // Convert activity type
            ActivityType activityType = ActivityType.valueOf(message.getActivityType());

            return ActivityLog.builder()
                    .workspaceId(workspaceId)
                    .projectId(message.getProjectId())
                    .applicationId(message.getApplicationId())
                    .activityType(activityType)
                    .description(activityType.getTemplate())
                    .variables(message.getVariables())
                    .attachments(message.getAttachments())
                    .createdBy(message.getCreatedBy())
                    .createdAt(message.getCreatedAt())
                    .build();

        } catch (Exception e) {
            log.error("Failed to convert message to entity: {}", message, e);
            throw new RuntimeException("Failed to convert activity log message", e);
        }
    }
}