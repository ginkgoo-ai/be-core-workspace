CREATE TABLE activity_log
(
    id             VARCHAR(36)  NOT NULL,
    activity_type  VARCHAR(100) NOT NULL,
    description    VARCHAR(500),
    workspace_id   VARCHAR(36)  NOT NULL,
    project_id     VARCHAR(36)  NOT NULL,
    application_id VARCHAR(36)  NOT NULL,
    context JSONB DEFAULT NULL,
    status         VARCHAR(20)  NOT NULL,
    created_by     VARCHAR(100) NOT NULL,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT workspace_pkey PRIMARY KEY (id),

    INDEX idx_workspace_id (workspace_id),
    INDEX idx_project_id (project_id),
    INDEX idx_application_id (application_id),
    INDEX idx_activity_type (activity_type),
    INDEX idx_operator_id (created_by),
    INDEX idx_occurred_at (created_at)
);