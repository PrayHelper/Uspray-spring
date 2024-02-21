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