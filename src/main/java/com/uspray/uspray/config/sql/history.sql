--6번째로 실행
create table history
(
    history_id     bigserial
        primary key,
    created_at     timestamp,
    updated_at     timestamp,
    category_id    bigint,
    content        varchar(255),
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