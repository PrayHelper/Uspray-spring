package com.uspray.uspray.config.sql;

public class PrayQueries {

    // pray -> temp, storage -> pray 로 변경
    public static final String ALTER_PRAY_TABLE =
        "ALTER TABLE pray RENAME TO temp_pray;" +
            "ALTER TABLE storage RENAME TO pray;";

    // 이후로 언급하는 pray는 모두 이전 storage 테이블
    public static final String ADD_FK_PRAY_TABLE =
        "ALTER TABLE pray RENAME COLUMN id TO pray_id;" + // storage.id -> pray.pray_id
            "ALTER TABLE PRAY ADD COLUMN member_id BIGINT;" +
            "ALTER TABLE PRAY ADD CONSTRAINT fk_member FOREIGN KEY (member_id) REFERENCES member(id);";

    // 필요한 컬럼 추가하기 & original_pray_id default는 pray_id로 설정
    public static final String INSERT_COLUMN_PRAY =
        "ALTER TABLE pray ADD COLUMN original_pray_id BIGINT;" +
            "UPDATE TABLE SET original_pray_id = pray_id;" +
            "ALTER TABLE pray RENAME COLUMN pray_cnt to count;" +
            "ALTER TABLE pray ALTER COLUMN deadline TYPE DATE USING to_date(deadline, 'YYYY-MM-DD');"
            +
            "ALTER TABLE pray ADD COLUMN deleted BOOLEAN;" +
            "UPDATE pray SET deleted = (deleted_at IS NOT NULL);" +
            "ALTER TABLE pray DROP COLUMN deleted_at;" +
            "ALTER TABLE pray ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;"
            +
            "ALTER TABLE pray ADD COLUMN pray_type VARCHAR(255);";


    // 이렇게 하면 인코딩이 안 될 것 같습니다. "6raM7J2A7Zic: NQ==" 이런 식으로 되어버립니다.
    // 우선 이렇게 합쳐놓고 인코딩 할때 :로 분리해서 디코딩한 후 다시 합쳐서 인코딩 하는 과정이 필요할 것 같습니다.
    public static final String CREATE_CONTENT_WITH_CONCAT =
        "ALTER TABLE pray ADD COLUMN content VARCHAR(255);" +
            "ALTER TABLE pray ADD COLUMN is_shared BOOLEAN;" +
            "UPDATE storage AS s" +
            "SET content = (tp.target || ': ' || tp.title)," +
            "is_shared = tp.is_shared" +
            "FROM temp_pray AS tp" +
            "WHERE length(tp.target || ': ' || tp.title) < 255" +
            "AND s.pray_id = tp.id;";
//            +
//            "ALTER TABLE pray DROP COLUMN title;" +
//            "ALTER TABLE pray DROP COLUMN target;";
}

// TODO: Complete 처리하기, pray_type 추가하기, category_type 추가하기, share 처리하기, history 추가하기
