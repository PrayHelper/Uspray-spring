-- MERGE STORAGE TABLE AND PRAY TABLE TO PRAY TABLE
ALTER TABLE pray RENAME TO temp_pray;
ALTER TABLE storage RENAME TO pray;
ALTER TABLE pray RENAME COLUMN id TO pray_id;

-- ADD COLUMN
ALTER TABLE pray ADD COLUMN original_pray_id BIGINT;
UPDATE TABLE SET origin_pray_id = pray_id;
ALTER TABLE pray RENAME COLUMN pray_cnt to count;
ALTER TABLE pray ALTER COLUMN deadline TYPE DATE USING to_date(deadline, 'YYYY-MM-DD');
ALTER TABLE pray ADD COLUMN deleted BOOLEAN;
UPDATE pray SET deleted = (deleted_at IS NOT NULL);
ALTER TABLE pray DROP COLUMN deleted_at;
ALTER TABLE pray ADD COLUMN last_prayed_at DATE;
ALTER TABLE pray ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE pray ADD COLUMN pray_type VARCHAR(255);

-- CREATE CONTENT
ALTER TABLE pray ADD COLUMN content VARCHAR(255);
ALTER TABLE pray ADD COLUMN is_shared BOOLEAN;
UPDATE pray AS p
SET content = tp.title,
    is_shared = tp.is_shared
    FROM temp_pray AS tp
AND p.pray_id = tp.id;
ALTER TABLE pray DROP COLUMN title;
-- ALTER TABLE pray DROP COLUMN target;

-- HANDLE COMPLETE PRAY
UPDATE pray
SET last_prayed_at = TO_DATE(c.created_at)
    FROM complete AS c
WHERE pray.pray_id = c.storage_id;

-- HANDLE PRAY TYPE
UPDATE pray
SET pray_type = CASE
    WHEN is_shared THEN 'shared'
    ELSE 'personal'
    END;
