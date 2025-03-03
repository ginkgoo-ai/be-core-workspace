-- Add logo_file_id columns to workspace table
ALTER TABLE workspace
    ADD COLUMN logo_file_id VARCHAR(36);

