CREATE TABLE workspace
(
    id          VARCHAR(36)  NOT NULL,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    logo_url    VARCHAR(255),
    owner_id    VARCHAR(36)  NOT NULL,
    status      VARCHAR(20)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_workspace PRIMARY KEY (id)
);

CREATE TABLE workspace_invitation
(
    id           VARCHAR(255) NOT NULL,
    email        VARCHAR(255) NOT NULL,
    workspace_id VARCHAR(36)  NOT NULL,
    invited_by   VARCHAR(255) NOT NULL,
    role         VARCHAR(255) NOT NULL,
    status       VARCHAR(255) NOT NULL,
    accepted_at  TIMESTAMP(6),
    expires_at   TIMESTAMP(6),
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_workspace_invitation PRIMARY KEY (id)
);

CREATE TABLE workspace_member
(
    id               VARCHAR(255) NOT NULL,
    user_id          VARCHAR(36)  NOT NULL,
    workspace_id     VARCHAR(36)  NOT NULL,
    role             VARCHAR(20)  NOT NULL,
    last_accessed_at TIMESTAMP(6),
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_workspace_member PRIMARY KEY (id),
    CONSTRAINT uk_workspace_member_user_workspace UNIQUE (workspace_id, user_id)
);

CREATE TABLE activity_log
(
    id             VARCHAR(36)  NOT NULL,
    activity_type  VARCHAR(100) NOT NULL,
    description    VARCHAR(500),
    workspace_id   VARCHAR(36)  NOT NULL,
    project_id     VARCHAR(36),
    application_id VARCHAR(36),
    context JSONB DEFAULT NULL,
    created_by     VARCHAR(100) NOT NULL,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_activity_log PRIMARY KEY (id)
);

-- Create indexes with standardized naming convention
CREATE INDEX idx_workspace_created_at ON workspace (created_at);
CREATE INDEX idx_workspace_owner_id ON workspace (owner_id);
CREATE INDEX idx_workspace_member_user_id ON workspace_member (user_id);
CREATE INDEX idx_workspace_member_workspace_id ON workspace_member (workspace_id);
CREATE INDEX idx_activity_log_workspace_id ON activity_log (workspace_id);
CREATE INDEX idx_activity_log_project_id ON activity_log (project_id);
CREATE INDEX idx_activity_log_application_id ON activity_log (application_id);
CREATE INDEX idx_activity_log_activity_type ON activity_log (activity_type);
CREATE INDEX idx_activity_log_created_by ON activity_log (created_by);
CREATE INDEX idx_activity_log_created_at ON activity_log (created_at);
