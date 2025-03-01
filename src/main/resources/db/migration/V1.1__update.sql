-- Add variables and attachments columns to activity_log table
ALTER TABLE activity_log
    ADD COLUMN variables JSONB,
    ADD COLUMN attachments JSONB;

ALTER TABLE activity_log 
    DROP COLUMN context;

-- Add indexes for JSON fields to improve query performance
CREATE INDEX idx_activity_log_variables ON activity_log USING gin (variables);
CREATE INDEX idx_activity_log_attachments ON activity_log USING gin (attachments);
