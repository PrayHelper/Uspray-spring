-- 3번째 실행
-- MERGE STORAGE TABLE AND PRAY TABLE TO PRAY TABLE
DELETE FROM share
WHERE (receipt_id, pray_id) IN (
    SELECT s.receipt_id, s.pray_id
    FROM share AS s
             JOIN storage AS p ON s.receipt_id = p.user_id AND s.pray_id = p.pray_id
);

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
UPDATE pray SET last_prayed_at = created_at;
ALTER TABLE pray ADD COLUMN updated_at TIMESTAMP;
ALTER TABLE pray ADD COLUMN pray_type VARCHAR(255);
ALTER TABLE pray ADD COLUMN temp_origin_pray_id BIGINT;

-- CREATE CONTENT
ALTER TABLE pray ADD COLUMN content VARCHAR(255);
ALTER TABLE pray ADD COLUMN is_shared BOOLEAN;
ALTER TABLE pray ALTER COLUMN content TYPE TEXT;
UPDATE pray AS p
SET content = tp.title,
    is_shared = tp.is_shared
    FROM temp_pray AS tp
WHERE p.origin_pray_id = tp.id;
-- ALTER TABLE temp_pray DROP COLUMN title;
-- ALTER TABLE temp_pray DROP COLUMN target;

-- HANDLE COMPLETE PRAY
UPDATE pray
SET last_prayed_at = c.created_at::date
FROM complete AS c
WHERE pray.pray_id = c.storage_id;

-- SET UPDATED_AT
UPDATE pray
SET updated_at = CASE
    WHEN last_prayed_at IS NOT NULL THEN last_prayed_at
    ELSE created_at
    END;

-- origin pray id 정리
ALTER TABLE pray ALTER COLUMN origin_pray_id DROP NOT NULL;

DROP TABLE IF EXISTS first_origin_pray_table;
CREATE TABLE first_origin_pray_table AS
SELECT MIN(pray_id) AS "min_pray_id" , origin_pray_id
FROM pray
GROUP BY origin_pray_id
ORDER BY MIN(created_at);

DO $$
DECLARE
origin_pray pray%rowtype;
    pray_record pray%rowtype;
BEGIN
UPDATE pray AS p
SET origin_pray_id = fop.min_pray_id,
    temp_origin_pray_id = fop.origin_pray_id
    FROM first_origin_pray_table AS fop
WHERE p.origin_pray_id = fop.origin_pray_id;
UPDATE pray AS p2
SET origin_pray_id = NULL,
    temp_origin_pray_id = fop2.origin_pray_id
    FROM first_origin_pray_table AS fop2
WHERE p2.pray_id = fop2.min_pray_id;
-- 결과를 출력하거나 다른 작업을 수행
RAISE NOTICE 'origin_pray_id finish';
END $$;


-- HANDLE PRAY TYPE
UPDATE pray
SET pray_type = CASE
    WHEN origin_pray_id IS NULL THEN 'PERSONAL'
    ELSE 'SHARED'
    END;
END;

ALTER TABLE pray DROP COLUMN temp_origin_pray_id;
DROP TABLE first_origin_pray_table;

UPDATE pray SET deleted = true WHERE pray.deadline < CURRENT_DATE;