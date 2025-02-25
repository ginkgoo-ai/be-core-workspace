package com.ginkgooai.core.workspace.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ginkgooai.core.common.bean.ActivityType;
import com.ginkgooai.core.common.message.ActivityLogMessage;
import com.ginkgooai.core.workspace.domain.ActivityLog;
import com.ginkgooai.core.workspace.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Activity log message processor
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityLogProcessor {

    private final ActivityLogRepository repository;

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    /**
     * Process activity log message
     */
    @Transactional
    public void processMessage(ActivityLogMessage message) {
        try {
            // Save to database
            ActivityLog activityLog = convertToEntity(message);
            repository.save(activityLog);

            log.debug("Successfully processed activity log: {}", message.getActivityType());
        } catch (Exception e) {
            log.error("Failed to process activity log message: {}", message, e);
        }
    }


    private ActivityLog convertToEntity(ActivityLogMessage message) throws JsonProcessingException {
        return ActivityLog.builder()
                .workspaceId(message.getWorkspaceId())
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
