DROP TABLE IF EXISTS workspace.workspace_member;
DROP TABLE IF EXISTS workspace.workspace_invitation;

-- Add created_by column first (without NOT NULL constraint initially)
ALTER TABLE workspace.workspace
    ADD COLUMN created_by VARCHAR(100);

-- Copy owner_id values to created_by
UPDATE workspace.workspace
SET created_by = owner_id;

-- Now drop owner_id column
ALTER TABLE workspace.workspace
    DROP COLUMN owner_id;

-- Add NOT NULL constraint to created_by after data migration
ALTER TABLE workspace.workspace
    ALTER COLUMN created_by SET NOT NULL;

-- Add updated_by column
ALTER TABLE workspace.workspace
    ADD COLUMN updated_by VARCHAR(100);

-- Add updated_by column to activity_log
ALTER TABLE workspace.activity_log
    ADD COLUMN updated_by VARCHAR(100);