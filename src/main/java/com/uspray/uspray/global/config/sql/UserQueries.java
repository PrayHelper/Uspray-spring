package com.uspray.uspray.global.config.sql;

public class UserQueries {

    public static final String ALTER_USER_TABLE =
        "ALTER TABLE \"user\" RENAME TO member;" +
            "ALTER TABLE member RENAME COLUMN uid TO user_id;" +
            "ALTER TABLE member ALTER COLUMN user_id TYPE VARCHAR(255);" +
            "ALTER TABLE member ALTER COLUMN password TYPE VARCHAR(255);" +
            "ALTER TABLE member ALTER COLUMN name TYPE VARCHAR(255);" +
            "ALTER TABLE member ALTER COLUMN gender TYPE VARCHAR(255);" +
            "ALTER TABLE member ALTER COLUMN birth TYPE VARCHAR(255) USING to_char(birth, 'YYYY-MM-DD');"
            +
            "ALTER TABLE member ALTER COLUMN phone TYPE VARCHAR(255);" +
            "ALTER TABLE member RENAME COLUMN device_token TO firebase_token;" +
            "ALTER TABLE member ALTER COLUMN firebase_token TYPE VARCHAR(255);" +

            "ALTER TABLE member ADD COLUMN deleted BOOLEAN;" +
            "UPDATE member SET deleted = (deleted_at IS NOT NULL);" +
            "ALTER TABLE member DROP COLUMN deleted_at;" +
            "ALTER TABLE member DROP COLUMN reset_pw;" +
            "ALTER TABLE member ADD COLUMN authority VARCHAR(255);" +
            "ALTER TABLE member ADD COLUMN social_id VARCHAR(255);" +
            "ALTER TABLE member ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;";

    public static final String ALTER_PRIMARY_KEY =
        "ALTER TABLE member ADD COLUMN member_id UNIQUE BIGSERIAL;" +
            "ALTER TABLE member ADD CONSTRAINT member_id_unique UNIQUE (member_id);";

    public static final String ALTER_USER_NOTIFICATION =
        "ALTER TABLE member ADD COLUMN first_noti_agree BOOLEAN DEFAULT TRUE;" +
            "ALTER TABLE member ADD COLUMN second_noti_agree BOOLEAN DEFAULT TRUE;" +
            "ALTER TABLE member ADD COLUMN third_noti_agree BOOLEAN DEFAULT TRUE;";

    public static final String INSERT_USER_NOTIFICATION =
        "UPDATE member"
            + "SET first_noti_agree = false"
            + "FROM user_notification"
            + "WHERE member.id = user_notification.user_id"
            + "AND user_notification.notification_id = 1"
            + "AND user_notification.is_enabled = false;"

            + "UPDATE member"
            + "SET second_noti_agree = false"
            + "FROM user_notification"
            + "WHERE member.id = user_notification.user_id"
            + "AND user_notification.notification_id = 2"
            + "AND user_notification.is_enabled = false"

            + "UPDATE member"
            + "SET third_noti_agree = false"
            + "FROM user_notification"
            + "WHERE member.id = user_notification.user_id"
            + "AND user_notification.notification_id = 3"
            + "AND user_notification.is_enabled = false;";

}
