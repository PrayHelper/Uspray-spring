-- 가장 먼저 실행
-- 1. 외래 키 제약 조건 제거
ALTER TABLE pray DROP CONSTRAINT pk_pray CASCADE;
ALTER TABLE complete DROP CONSTRAINT fk_complete_user_id_user;
ALTER TABLE storage DROP CONSTRAINT fk_storage_user_id_user;
ALTER TABLE share DROP CONSTRAINT fk_share_receipt_id_user;

-- 2. 기본 키의 데이터 타입 변경
-- create sequence
ALTER TABLE "user" ADD COLUMN member_id BIGSERIAL UNIQUE;
ALTER TABLE "user" DROP CONSTRAINT pk_user CASCADE;
ALTER TABLE "user" ADD PRIMARY KEY (member_id);

-- 3. 트리거 생성
CREATE OR REPLACE FUNCTION update_pray_user_id()
RETURNS TRIGGER AS $$
BEGIN
    -- 변경된 member_id 값을 사용하여 pray 테이블의 user_id를 업데이트
UPDATE pray
SET user_id = NEW.member_id
WHERE user_id = OLD.user_id;

UPDATE complete
SET user_id = NEW.user_id
WHERE user_id = OLD.user_id;

UPDATE storage
SET user_id = NEW.user_id
WHERE user_id = OLD.user_id;

UPDATE share
SET receipt_id = NEW.user_id
WHERE receipt_id = OLD.user_id;


RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS pray_user_id_update_trigger ON "user";
CREATE TRIGGER pray_user_id_update_trigger
    AFTER UPDATE OF id ON "user"
    FOR EACH ROW
    EXECUTE FUNCTION update_pray_user_id();

-- 4. 새로운 외래 키 추가 (pray table)
ALTER TABLE pray ALTER COLUMN user_id TYPE varchar(255);
UPDATE pray SET user_id = u.member_id FROM "user" u WHERE pray.user_id = u.id::varchar(255);
ALTER TABLE pray ALTER COLUMN user_id TYPE bigint USING user_id::bigint;
ALTER TABLE pray ADD CONSTRAINT pk_pray FOREIGN KEY (user_id) REFERENCES "user"(member_id) ON UPDATE CASCADE;
ALTER TABLE pray RENAME COLUMN user_id TO member_id;

ALTER TABLE complete ALTER COLUMN user_id TYPE varchar(255);
UPDATE complete SET user_id = u.member_id FROM "user" u WHERE complete.user_id = u.id::varchar(255);
ALTER TABLE complete ALTER COLUMN user_id TYPE bigint USING user_id::bigint;
ALTER TABLE complete ADD CONSTRAINT fk_complete_user_id_user FOREIGN KEY (user_id) REFERENCES "user"(member_id) ON UPDATE CASCADE;
ALTER TABLE complete RENAME COLUMN user_id TO member_id;

ALTER TABLE storage ALTER COLUMN user_id TYPE varchar(255);
UPDATE storage SET user_id = u.member_id FROM "user" u WHERE storage.user_id = u.id::varchar(255);
ALTER TABLE storage ALTER COLUMN user_id TYPE bigint USING user_id::bigint;
ALTER TABLE storage ADD CONSTRAINT fk_storage_user_id_user FOREIGN KEY (user_id) REFERENCES "user"(member_id) ON UPDATE CASCADE;
ALTER TABLE storage RENAME COLUMN user_id TO member_id;

ALTER TABLE share ALTER COLUMN receipt_id TYPE varchar(255);
UPDATE share SET receipt_id = u.member_id FROM "user" u WHERE share.receipt_id = u.id::varchar(255);
ALTER TABLE share ALTER COLUMN receipt_id TYPE bigint USING receipt_id::bigint;
ALTER TABLE share ADD CONSTRAINT fk_share_receipt_id_user FOREIGN KEY (receipt_id) REFERENCES "user"(member_id) ON UPDATE CASCADE;
ALTER TABLE share RENAME COLUMN receipt_id TO member_id;

