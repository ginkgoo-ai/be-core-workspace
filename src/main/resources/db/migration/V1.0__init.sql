-- V1__Initial_Schema.sql

-- Workspace table
CREATE TABLE workspace (
                           id VARCHAR(36) NOT NULL PRIMARY KEY,
                           name VARCHAR(100) NOT NULL,
                           description VARCHAR(500),
                           logo_url VARCHAR(255),
                           secondary_logo_url VARCHAR(255),
                           portal_preview_logo_type VARCHAR(20) DEFAULT 'PRIMARY',
                           shortlist_preview_logo_type VARCHAR(20) DEFAULT 'PRIMARY',
                           owner_id VARCHAR(36) NOT NULL,
                           status VARCHAR(20) NOT NULL,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_workspace_created_at ON workspace (created_at);
CREATE INDEX idx_workspace_owner_id ON workspace (owner_id);

-- Workspace invitation table
CREATE TABLE workspace_invitation (
                                      id VARCHAR(255) NOT NULL PRIMARY KEY,
                                      email VARCHAR(255) NOT NULL,
                                      workspace_id VARCHAR(36) NOT NULL,
                                      invited_by VARCHAR(255) NOT NULL,
                                      role VARCHAR(255) NOT NULL,
                                      status VARCHAR(255) NOT NULL,
                                      accepted_at TIMESTAMP(6),
                                      expires_at TIMESTAMP(6),
                                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Workspace member table
CREATE TABLE workspace_member (
                                  id VARCHAR(255) NOT NULL PRIMARY KEY,
                                  user_id VARCHAR(36) NOT NULL,
                                  workspace_id VARCHAR(36) NOT NULL,
                                  role VARCHAR(20) NOT NULL,
                                  last_accessed_at TIMESTAMP(6),
                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  UNIQUE (workspace_id, user_id)
);

CREATE INDEX idx_workspace_member_user_id ON workspace_member (user_id);
CREATE INDEX idx_workspace_member_workspace_id ON workspace_member (workspace_id);

-- Activity log table
CREATE TABLE activity_log (
                              id VARCHAR(36) NOT NULL PRIMARY KEY,
                              activity_type VARCHAR(100) NOT NULL,
                              description VARCHAR(500),
                              workspace_id VARCHAR(36) NOT NULL,
                              project_id VARCHAR(36),
                              application_id VARCHAR(36),
                              created_by VARCHAR(100) NOT NULL,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              variables JSONB,
                              attachments JSONB
);

CREATE INDEX idx_activity_log_workspace_id ON activity_log (workspace_id);
CREATE INDEX idx_activity_log_project_id ON activity_log (project_id);
CREATE INDEX idx_activity_log_application_id ON activity_log (application_id);
CREATE INDEX idx_activity_log_activity_type ON activity_log (activity_type);
CREATE INDEX idx_activity_log_created_by ON activity_log (created_by);
CREATE INDEX idx_activity_log_created_at ON activity_log (created_at);
CREATE INDEX idx_activity_log_variables ON activity_log USING GIN (variables);
CREATE INDEX idx_activity_log_attachments ON activity_log USING GIN (attachments);
