CREATE TABLE workspace (
                           id VARCHAR(36) NOT NULL,
                           description VARCHAR(500),
                           logo_url VARCHAR(255),
                           name VARCHAR(100) NOT NULL,
                           owner_id VARCHAR(36) NOT NULL,
                           status VARCHAR(20) NOT NULL,
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           CONSTRAINT workspace_status_check CHECK (status IN ('ACTIVE', 'INACTIVE', 'DELETED')),
                           CONSTRAINT workspace_pkey PRIMARY KEY (id)
);

CREATE TABLE workspace_invitation (
                                      id VARCHAR(255) NOT NULL,
                                      accepted_at TIMESTAMP(6),
                                      email VARCHAR(255) NOT NULL,
                                      expires_at TIMESTAMP(6),
                                      invited_by VARCHAR(255) NOT NULL,
                                      role VARCHAR(255) NOT NULL,
                                      status VARCHAR(255) NOT NULL,
                                      workspace_id VARCHAR(36) NOT NULL,
                                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      CONSTRAINT workspace_invitation_role_check CHECK (role IN ('OWNER', 'ADMIN', 'MEMBER')),
                                      CONSTRAINT workspace_invitation_status_check CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'EXPIRED')),
                                      CONSTRAINT workspace_invitation_pkey PRIMARY KEY (id)
);

CREATE TABLE workspace_member (
                                  id VARCHAR(255) NOT NULL,
                                  last_accessed_at TIMESTAMP(6) ,
                                  role VARCHAR(20) NOT NULL,
                                  user_id VARCHAR(36) NOT NULL,
                                  workspace_id VARCHAR(36) NOT NULL,
                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  CONSTRAINT workspace_member_role_check CHECK (role IN ('OWNER', 'ADMIN', 'MEMBER')),
                                  CONSTRAINT workspace_member_pkey PRIMARY KEY (id),
                                  CONSTRAINT ukehbe3e8pf5wxry5hs5aa5hona UNIQUE (workspace_id, user_id)
);

-- Create indexes
CREATE INDEX idx_createdat ON workspace(created_at);
CREATE INDEX idx_owner ON workspace(owner_id);
CREATE INDEX idx_workspace_member_user ON workspace_member(user_id);
CREATE INDEX idx_workspace_member_workspace ON workspace_member(workspace_id);

-- Add foreign key constraints
ALTER TABLE workspace_invitation
    ADD CONSTRAINT fkmqjx2gtti3uaa60n8msuicmyr
        FOREIGN KEY (workspace_id)
            REFERENCES workspace(id);

ALTER TABLE workspace_member
    ADD CONSTRAINT fkqt5hp8najyhtbees6p095nn16
        FOREIGN KEY (workspace_id)
            REFERENCES workspace(id);
