-- Add secondary_logo_url and preview logo type settings, remove logo_file_id
ALTER TABLE workspace
    ADD COLUMN secondary_logo_url VARCHAR(255),
    ADD COLUMN portal_preview_logo_type VARCHAR(20) DEFAULT 'PRIMARY',
    ADD COLUMN shortlist_preview_logo_type VARCHAR(20) DEFAULT 'PRIMARY',
    DROP COLUMN logo_file_id;