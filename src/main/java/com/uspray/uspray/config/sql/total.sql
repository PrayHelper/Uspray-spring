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
DROP TABLE member;
ALTER TABLE "user" RENAME TO member;

ALTER TABLE pray ADD COLUMN member_id bigint;
UPDATE pray SET member_id = u.member_id FROM member u WHERE pray.user_id = u.id;
ALTER TABLE pray ADD CONSTRAINT pk_pray FOREIGN KEY (member_id) REFERENCES member(member_id) ON UPDATE CASCADE;
-- ALTER TABLE pray DROP COLUMN user_id;

ALTER TABLE complete ADD COLUMN member_id bigint;
UPDATE complete SET member_id = u.member_id FROM member u WHERE complete.user_id = u.id;
ALTER TABLE complete ADD CONSTRAINT fk_complete_member_id_member FOREIGN KEY (member_id) REFERENCES member(member_id) ON UPDATE CASCADE;
-- ALTER TABLE complete DROP COLUMN user_id;
--
ALTER TABLE storage ADD COLUMN member_id bigint;
UPDATE storage SET member_id = u.member_id FROM member u WHERE storage.user_id = u.id;
ALTER TABLE storage ADD CONSTRAINT fk_storage_member_id_member FOREIGN KEY (member_id) REFERENCES member(member_id) ON UPDATE CASCADE;
-- ALTER TABLE storage DROP COLUMN user_id;

ALTER TABLE share ADD COLUMN member_id bigint;
UPDATE share SET member_id = u.member_id FROM member u WHERE share.receipt_id = u.id;
ALTER TABLE share ADD CONSTRAINT fk_share_member_id_member FOREIGN KEY (member_id) REFERENCES member(member_id) ON UPDATE CASCADE;
-- ALTER TABLE share DROP COLUMN receipt_id;

-- 2번째로 실행
-- ALTER USER TABLE TO MEMBER TABLE AND ADD COLUMN

ALTER TABLE member RENAME COLUMN uid TO user_id;
ALTER TABLE member ALTER COLUMN user_id TYPE VARCHAR(255);
ALTER TABLE member ALTER COLUMN password TYPE VARCHAR(255);
ALTER TABLE member ALTER COLUMN name TYPE VARCHAR(255);
ALTER TABLE member ALTER COLUMN gender TYPE VARCHAR(255);
ALTER TABLE member ALTER COLUMN birth TYPE VARCHAR(255) USING to_char(birth, 'YYYY-MM-DD');
ALTER TABLE member ALTER COLUMN phone TYPE VARCHAR(255);
ALTER TABLE member RENAME COLUMN device_token TO firebase_token;
ALTER TABLE member ALTER COLUMN firebase_token TYPE VARCHAR(255);
ALTER TABLE member ADD COLUMN deleted BOOLEAN;
UPDATE member SET deleted = (deleted_at IS NOT NULL);
ALTER TABLE member DROP COLUMN deleted_at;
ALTER TABLE member DROP COLUMN reset_pw;
ALTER TABLE member ADD COLUMN authority VARCHAR(255);
UPDATE member
SET authority = 'ROLE_USER';
ALTER TABLE member ADD COLUMN social_id VARCHAR(255);
ALTER TABLE member ADD COLUMN updated_at TIMESTAMP;

-- HANDLE NOTIFICATION AGREEMENT
ALTER TABLE member ADD COLUMN first_noti_agree BOOLEAN DEFAULT TRUE;
ALTER TABLE member ADD COLUMN second_noti_agree BOOLEAN DEFAULT TRUE;
ALTER TABLE member ADD COLUMN third_noti_agree BOOLEAN DEFAULT TRUE;
UPDATE member
SET first_noti_agree = user_notification.is_enabled,
    updated_at = user_notification.updated_at
    FROM user_notification
WHERE member.id = user_notification.user_id
  AND user_notification.notification_id = 1;
UPDATE member
SET second_noti_agree = user_notification.is_enabled,
    updated_at = user_notification.updated_at
    FROM user_notification
WHERE member.id = user_notification.user_id
  AND user_notification.notification_id = 2;

UPDATE member
SET third_noti_agree = user_notification.is_enabled,
    updated_at = user_notification.updated_at
    FROM user_notification
WHERE member.id = user_notification.user_id
  AND user_notification.notification_id = 3
  AND user_notification.is_enabled = false;

-- 3번째 실행
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

--4번째 실행
ALTER TABLE share
    RENAME TO shared_pray;

ALTER TABLE shared_pray
DROP CONSTRAINT fk_share_storage_id_storage;
ALTER TABLE shared_pray
DROP CONSTRAINT pk_share;

-- 1. shared_pray_id 열 추가
ALTER TABLE shared_pray
    ADD COLUMN shared_pray_id BIGSERIAL UNIQUE;

-- 2. shared_pray_id 열이 NULL이 아닌 행을 선택하여 시퀀스 변경
-- DROP SEQUENCE shared_pray_shared_pray_id_seq;
-- ALTER COLUMN shared_pray_id SET DATA TYPE BIGSERIAL;
-- CREATE SEQUENCE shared_pray_shared_pray_id_seq AS integer;
-- SELECT SETVAL('shared_pray_shared_pray_id_seq', COALESCE(MAX(shared_pray_id), 0) + 1)
-- FROM shared_pray;

-- 3. 시퀀스를 사용하여 shared_pray_id 열 업데이트
-- UPDATE shared_pray
-- SET shared_pray_id = nextval('shared_pray_shared_pray_id_seq');

-- 4. shared_pray_id 열을 기본 키로 설정
ALTER TABLE shared_pray
    ADD PRIMARY KEY (shared_pray_id);

--EDIT COLUMN
DELETE
FROM shared_pray
WHERE deleted_at IS NOT NULL;
ALTER TABLE shared_pray
DROP COLUMN deleted_at;
ALTER TABLE shared_pray
    ADD COLUMN updated_at TIMESTAMP;
UPDATE shared_pray
SET updated_at = created_at;
ALTER TABLE shared_pray
    RENAME COLUMN pray_id TO legacy_pray_id;
ALTER TABLE shared_pray
    RENAME COLUMN storage_id TO pray_id;
ALTER TABLE shared_pray
ALTER COLUMN pray_id TYPE BIGINT;

ALTER TABLE shared_pray
DROP COLUMN receipt_id;
ALTER TABLE shared_pray
DROP COLUMN legacy_pray_id;

ALTER TABLE shared_pray
    ADD CONSTRAINT fk_shared_pray_pray_id_pray FOREIGN KEY (pray_id) REFERENCES pray (pray_id);

--5번째로 실행
create table category
(
    category_id    bigserial
        primary key,
    created_at     timestamp,
    updated_at     timestamp,
    category_type  varchar(255) not null,
    color          varchar(255),
    deleted        boolean,
    name           varchar(255),
    category_order integer,
    member_id      bigint       not null
        constraint category_member_id_fk
            references member
);

INSERT INTO category (created_at, updated_at, category_type, color, deleted, name, category_order, member_id)
SELECT CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'PERSONAL', '#75BD62', false, '기본 카테고리', 1024, member_id
FROM member;

INSERT INTO category (created_at, updated_at, category_type, color, deleted, name, category_order, member_id)
SELECT CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SHARED', '#649D55', false, '기본 공유 카테고리', 1024, member_id
FROM member;

ALTER TABLE pray ADD COLUMN category_id BIGINT;
ALTER TABLE pray ADD CONSTRAINT pray_category_id_fk FOREIGN KEY (category_id) REFERENCES category (category_id);

UPDATE pray SET category_id = c.category_id
    FROM category AS c
WHERE pray.member_id = c.member_id AND c.category_type = 'PERSONAL' AND pray.pray_type = 'PERSONAL';

UPDATE pray SET category_id = c.category_id
    FROM category AS c
WHERE pray.member_id = c.member_id AND c.category_type = 'SHARED' AND pray.pray_type = 'SHARED';

ALTER TABLE member ALTER COLUMN id DROP NOT NULL;
ALTER TABLE pray ALTER COLUMN user_id DROP NOT NULL;

--6번째로 실행
create table history
(
    history_id     bigserial
        primary key,
    created_at     timestamp,
    updated_at     timestamp,
    category_id    bigint,
    content        TEXT,
    deadline       date,
    is_shared      boolean,
    origin_pray_id bigint,
    personal_count integer,
    pray_type      varchar(255) not null,
    total_count    integer,
    member_id      bigint       not null
        constraint history_member_id_fk
            references member
);

-- deadline 지난 pray는 history로 이동
INSERT INTO history (created_at, updated_at, category_id, content, deadline, is_shared,
                     origin_pray_id, personal_count, pray_type, total_count, member_id)
SELECT pray.deadline,
       pray.deadline,
       pray.category_id,
       pray.content,
       pray.deadline,
       pray.is_shared,
       pray.origin_pray_id,
       pray.count,
       pray.pray_type,
       --subquery 기도제목 원본(공유한 사람) 기도 횟수 카운트
       CAST(COALESCE((SELECT SUM(count)
                      FROM pray AS sub_pray
                      WHERE sub_pray.pray_id = pray.origin_pray_id), 0) AS int) +
           --subquery 기도제목 공유본(공유받은 사람) 기도 횟수 카운트
       CAST(COALESCE((SELECT SUM(count)
                      FROM pray AS sub_pray
                      WHERE sub_pray.origin_pray_id = pray.origin_pray_id),
                     0) AS int) AS total_count,
       pray.member_id
FROM pray
WHERE pray.deadline < CURRENT_DATE;


DELETE
FROM history
WHERE origin_pray_id IN (SELECT pray_id
                         FROM Pray
                         WHERE deleted = true);

-- 7번째로 실행
create table club
(
    group_id   bigserial
        primary key,
    created_at timestamp,
    updated_at timestamp,
    name       varchar(255),
    leader_id  bigint
        constraint club_member_id_fk
            references member
);

-- auto-generated definition
create table group_member
(
    groupmember_id     bigserial
        primary key,
    created_at         timestamp,
    updated_at         timestamp,
    notification_agree boolean,
    group_id           bigint
        constraint group_member_group_id_fk
            references club,
    member_id          bigint
        constraint group_member_member_id_fk
            references member
);

create table group_pray
(
    grouppray_id  bigserial
        primary key,
    created_at    timestamp,
    updated_at    timestamp,
    content       varchar(255),
    deadline      date,
    member_id     bigint
        constraint group_pray_member_id_fk
            references member,
    group_id      bigint
        constraint group_pray_group_id_fk
            references club,
    pray_id       bigint
        constraint group_pray_pray_id_fk
            references pray,
    group_pray_id bigint
        constraint group_pray_group_pray_id_fk
            references pray
);

create table member_group
(
    member_id bigint not null,
    group_id  bigint not null,
    primary key (member_id, group_id)
);

create table scrap_and_heart
(
    scrap_heart_id bigserial
        primary key,
    heart          boolean not null,
    scrap          boolean not null,
    grouppray_id   bigint
        constraint scrap_and_heart_grouppray_id_fk
            references group_pray,
    member_id      bigint
        constraint scrap_and_heart_member_id_fk
            references member,
    pray_id        bigint
        constraint scrap_and_heart_pray_id_fk
            references pray
);

create table withdraw
(
    withdraw_id     bigserial
        primary key,
    created_at      timestamp,
    updated_at      timestamp,
    description     varchar(255),
    member_id       bigint,
    withdraw_reason integer
);

DROP TABLE IF EXISTS test;
DROP TABLE IF EXISTS user_delete;
DROP TABLE IF EXISTS user_delete_reason;
DROP TABLE IF EXISTS temp_pray;
DROP TABLE IF EXISTS user_notification_log;
DROP TABLE IF EXISTS user_notification;
DROP TABLE IF EXISTS notification;

create table notification
(
    id    bigint not null
        primary key,
    body  varchar(255),
    image varchar(255),
    title varchar(255)
);
create table notification_log
(
    notification_log_id bigserial
        primary key,
    created_at          timestamp,
    updated_at          timestamp,
    title               varchar(255),
    member_id           bigint
        constraint notification_log_member_id_fk
            references member,
    pray_id             bigint
        constraint notification_log_pray_id_fk
            references pray
);