-- MERGE STORAGE TABLE AND PRAY TABLE TO PRAY TABLE
ALTER TABLE pray RENAME TO temp_pray;
ALTER TABLE storage RENAME TO pray;

-- ADD COLUMN
ALTER TABLE pray RENAME COLUMN pray_id TO origin_pray_id;
ALTER TABLE pray ALTER COLUMN origin_pray_id TYPE BIGINT;
ALTER TABLE pray RENAME COLUMN id TO pray_id;
ALTER TABLE pray ALTER COLUMN pray_id TYPE BIGINT;

ALTER TABLE pray RENAME COLUMN pray_cnt to count;
ALTER TABLE pray ALTER COLUMN deadline TYPE DATE USING (deadline::DATE);
ALTER TABLE pray ADD COLUMN deleted BOOLEAN;
UPDATE pray SET deleted = (deleted_at IS NOT NULL);
ALTER TABLE pray DROP COLUMN deleted_at;
ALTER TABLE pray ADD COLUMN last_prayed_at DATE;
ALTER TABLE pray ADD COLUMN updated_at TIMESTAMP;
ALTER TABLE pray ADD COLUMN pray_type VARCHAR(255);

-- CREATE CONTENT
ALTER TABLE pray ADD COLUMN content VARCHAR(255);
ALTER TABLE pray ADD COLUMN is_shared BOOLEAN;
UPDATE pray AS p
SET content = LEFT(tp.title, 255),
    is_shared = tp.is_shared
    FROM temp_pray AS tp
WHERE p.pray_id = tp.id;
-- ALTER TABLE temp_pray DROP COLUMN title;
-- ALTER TABLE temp_pray DROP COLUMN target;

-- HANDLE COMPLETE PRAY
UPDATE pray
SET last_prayed_at = c.created_at::date
FROM complete AS c
WHERE pray.pray_id = c.storage_id;

-- HANDLE PRAY TYPE
UPDATE pray
SET pray_type = CASE
    WHEN is_shared THEN 'shared'
    ELSE 'personal'
    END;

-- SET UPDATED_AT
UPDATE pray
SET updated_at = CASE
    WHEN last_prayed_at IS NOT NULL THEN last_prayed_at
    ELSE created_at
    END;