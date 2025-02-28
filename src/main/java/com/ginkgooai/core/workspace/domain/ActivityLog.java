package com.ginkgooai.core.workspace.domain;

import com.ginkgooai.core.common.bean.ActivityType;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "activity_log")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    private ActivityType activityType;

    private String description;

    private String workspaceId;

    private String projectId;

    private String applicationId;

    private String createdBy;

    @Type(JsonType.class)
//    @Column(columnDefinition = "jsonb")
    private Map<String, Object> context;
    
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "workspace_id", nullable = false)
//    private Workspace workspace;
    
}