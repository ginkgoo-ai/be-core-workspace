ALTER TABLE workspace
    ADD COLUMN domain VARCHAR(255);

UPDATE workspace
SET domain = LOWER(REGEXP_REPLACE(name, '[^a-zA-Z0-9]', '-'))
WHERE domain IS NULL;

ALTER TABLE workspace
    ADD CONSTRAINT uk_workspace_domain UNIQUE (domain);