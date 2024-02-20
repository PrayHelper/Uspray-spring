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
)

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