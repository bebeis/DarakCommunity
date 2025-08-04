-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
-- 1) 게시판 카테고리 (8개)
-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
INSERT INTO board_category
    (board_category_id, name, priority)
SELECT
    X,
    CASE
        WHEN X = 1 THEN 'General'
        WHEN X = 2 THEN 'Announcements'
        WHEN X = 3 THEN 'Off-topic'
        WHEN X = 4 THEN 'Questions'
        WHEN X = 5 THEN 'Events'
        WHEN X = 6 THEN 'Reviews'
        WHEN X = 7 THEN 'Tips'
        ELSE 'Random'
        END,
    X
FROM SYSTEM_RANGE(1,8) AS T(X);
ALTER TABLE board_category ALTER COLUMN board_category_id RESTART
WITH 9;

-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
-- 2) 게시판 (48개) - 8개 카테고리 × 6개 게시판
-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
INSERT INTO board
    (board_id, priority, board_category_id, name, description)
SELECT
    X,
    X,
    MOD(X-1,8) + 1,
    CONCAT('Board ', X),
    CONCAT('Description for board ', X)
FROM SYSTEM_RANGE(1,48) AS T(X);
ALTER TABLE board ALTER COLUMN board_id RESTART
WITH 49;

-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
-- 3) 회원 (1,000명) - MemberPassword가 @Embedded이므로 플래트 구조
-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
INSERT INTO member
    (
    member_id, birth, failed_count,
    created_date, expiration_date, last_modified_date,
    ttl, email, login_id, name, password, phone, member_grade
    )
SELECT
    X,
    DATEADD('DAY', -((MOD(X,50)+18)*365), CURRENT_DATE
()),
    MOD
(X,5),
    DATEADD
('DAY', -MOD
(X,365), CURRENT_TIMESTAMP
()),
    DATEADD
('DAY',  MOD
(X,365), CURRENT_TIMESTAMP
()),
    CURRENT_TIMESTAMP
(),
    2592000,
    CONCAT
('user', X, '@example.com'),
    CONCAT
('user', X),
    CONCAT
('User ', X),
    'password',
    CONCAT
('010-0000-', LPAD
(X,4,'0')),
    CASE
        WHEN MOD
(X,10)=0 THEN 'ADMIN'
        WHEN MOD
(X,5)=0  THEN 'MASTER'
        ELSE 'MEMBER'
END
FROM SYSTEM_RANGE
(1,1000) AS T
(X);

-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
-- 4) 게시글 (1,000개)
-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
INSERT INTO post
    (
    post_id, anonymous, board_id,
    created_date, member_id,
    read_count, content, title,
    post_type, last_modified_date
    )
SELECT
    X,
    CASE WHEN MOD(X,5)=0 THEN TRUE ELSE FALSE END,
    MOD(X,48) + 1,
    DATEADD('DAY', -MOD(X,365), CURRENT_TIMESTAMP
()),
    MOD
(X,1000) + 1,
    MOD
(X,1000) * 10,
    CONCAT
('Content of post ', X),
    CONCAT
('Title ', X),
    CASE
        WHEN MOD
(X,10)=0 THEN 'NOTICE'
        WHEN MOD
(X,5)=0  THEN 'FAQ'
        ELSE 'NORMAL'
END,
    CURRENT_TIMESTAMP
()
FROM SYSTEM_RANGE
(1,1000) AS T
(X);
ALTER TABLE post ALTER COLUMN post_id RESTART
WITH 1001;

-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
-- 5) 첨부파일 (500개) - UploadFile이 @Embedded이므로 플래트 구조
-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
INSERT INTO attachment
    (
    attachment_id, post_id, size,
    file_name, file_type, url,
    created_date, last_modified_date
    )
SELECT
    X,
    MOD(X,1000) + 1,
    MOD(X,1024) * 1024,
    CONCAT('file', X, '.txt'),
    'text/plain',
    CONCAT('http://example.com/files/file', X, '.txt'),
    CURRENT_TIMESTAMP
(),
    CURRENT_TIMESTAMP
()
FROM SYSTEM_RANGE
(1,500) AS T
(X);
ALTER TABLE attachment ALTER COLUMN attachment_id RESTART
WITH 501;

-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
-- 6) 관리자 로그 (500개)
-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
INSERT INTO admin_log
    (
    created_date, admin_log_id, last_modified_date,
    member_id, reason, type,
    after_content, before_content
    )
SELECT
    DATEADD('HOUR', -MOD(X,100), CURRENT_TIMESTAMP
()),
    X,
    CURRENT_TIMESTAMP
(),
    MOD
(X,1000) + 1,
    CASE
        WHEN MOD
(X,3)=0 THEN 'update'
        WHEN MOD
(X,3)=1 THEN 'delete'
        ELSE 'create'
END,
    CASE
        WHEN MOD
(X,3)=0 THEN 'INFO'
        WHEN MOD
(X,3)=1 THEN 'WARN'
        ELSE 'ERROR'
END,
    CONCAT
('after state ', X),
    CONCAT
('before state ', X)
FROM SYSTEM_RANGE
(1,500) AS T
(X);
ALTER TABLE admin_log ALTER COLUMN admin_log_id RESTART
WITH 501;

-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
-- 7) 게시판 즐겨찾기 (500개)
-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
INSERT INTO board_favorite
    (
    board_favorite_id, member_id, board_id, priority
    )
SELECT
    X,
    MOD(X,1000) + 1,
    MOD(X,48)  + 1,
    MOD(X,10)
FROM SYSTEM_RANGE(1,500) AS T(X);
ALTER TABLE board_favorite ALTER COLUMN board_favorite_id RESTART
WITH 501;

-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
-- 8) 댓글 (2,000개)
-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
INSERT INTO comment
    (
    comment_id, anonymous, created_date, last_modified_date,
    member_id, parent_id, post_id, content
    )
SELECT
    X,
    CASE WHEN MOD(X,4)=0 THEN TRUE ELSE FALSE END,
    DATEADD('DAY', -MOD(X,365), CURRENT_TIMESTAMP
()),
    CURRENT_TIMESTAMP
(),
    MOD
(X,1000) + 1,
    CASE WHEN MOD
(X,10)=0 THEN X-1 ELSE NULL
END,
    MOD
(X,1000) + 1,
    CONCAT
('Comment content ', X)
FROM SYSTEM_RANGE
(1,2000) AS T
(X);
ALTER TABLE comment ALTER COLUMN comment_id RESTART
WITH 2001;

-- 댓글 테이블의 AUTO_INCREMENT 시퀀스를 다음 사용 가능한 값으로 재설정

-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
-- 9) 댓글 좋아요 (1,000개) - Heart 부모 클래스에서 id 상속
-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
INSERT INTO comment_heart
    (
    id, comment_id, member_id,
    created_date, last_modified_date
    )
SELECT
    X,
    MOD(X,2000) + 1,
    MOD(X,1000) + 1,
    DATEADD('HOUR', -MOD(X,24), CURRENT_TIMESTAMP
()),
    CURRENT_TIMESTAMP
()
FROM SYSTEM_RANGE
(1,1000) AS T
(X);
ALTER TABLE comment_heart ALTER COLUMN id RESTART
WITH 1001;

-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
-- 10) 게시글 좋아요 (1,000개) - PostHeart 자체 id 필드
-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
INSERT INTO post_heart
    (
    id, member_id, post_id,
    created_date, last_modified_date
    )
SELECT
    X,
    MOD(X,1000) + 1,
    MOD(X,1000) + 1,
    DATEADD('HOUR', -MOD(X,24), CURRENT_TIMESTAMP
()),
    CURRENT_TIMESTAMP
()
FROM SYSTEM_RANGE
(1,1000) AS T
(X);
ALTER TABLE post_heart ALTER COLUMN id RESTART
WITH 1001;

-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
-- 11) 기프티콘 (200개)
-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
INSERT INTO gifticon
    (
    gifticon_id, title, description, brand, image_url,
    total_quantity, remaining_quantity,
    start_time, end_time, status,
    created_date, last_modified_date
    )
SELECT
    X,
    CONCAT('Gifticon Title ', X),
    CONCAT('Description for gifticon ', X),
    CONCAT('Brand', MOD(X,10)+1),
    CONCAT('http://example.com/images/gifticon', X, '.png'),
    100 + MOD(X,50),
    50  + MOD(X,50),
    DATEADD('DAY', -30 + MOD(X,60), CURRENT_TIMESTAMP
()),
    DATEADD
('DAY',  30 - MOD
(X,60), CURRENT_TIMESTAMP
()),
    CASE
        WHEN MOD
(X,4)=0 THEN 'ACTIVE'
        WHEN MOD
(X,4)=1 THEN 'WAITING'
        WHEN MOD
(X,4)=2 THEN 'SOLD_OUT'
        ELSE 'CLOSED'
END,
    DATEADD
('DAY', -10, CURRENT_TIMESTAMP
()),
    CURRENT_TIMESTAMP
()
FROM SYSTEM_RANGE
(1,200) AS T
(X);
ALTER TABLE gifticon ALTER COLUMN gifticon_id RESTART
WITH 201;

-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
-- 12) 기프티콘 클레임 (500개)
-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
INSERT INTO gifticon_claim
    (
    gifticon_claim_id, gifticon_id, member_id,
    gifticon_code, status, created_date, last_modified_date
    )
SELECT
    X,
    MOD(X,200)  + 1,
    MOD(X,1000) + 1,
    CONCAT('CODE', LPAD(X,6,'0')),
    CASE WHEN MOD(X,2)=0 THEN 'CLAIMED' ELSE 'USED' END,
    DATEADD('DAY', -MOD(X,365), CURRENT_TIMESTAMP
()),
    CURRENT_TIMESTAMP
()
FROM SYSTEM_RANGE
(1,500) AS T
(X);
ALTER TABLE gifticon_claim ALTER COLUMN gifticon_claim_id RESTART
WITH 501;

-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
-- 13) TestDataInit에서 추가되는 특별 계정들
-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

-- 관리자 계정 (TestDataInit.joinAdmin()에서 생성되는 데이터)
INSERT INTO member
    (
    member_id, birth, failed_count,
    created_date, expiration_date, last_modified_date,
    ttl, email, login_id, name, password, phone, member_grade
    )
VALUES
    (
        1001,
        '1990-01-01',
        0,
        CURRENT_TIMESTAMP
(), 
    DATEADD
('DAY', 14, CURRENT_TIMESTAMP
()), 
    CURRENT_TIMESTAMP
(),
    2592000, 
    'test@test.com', 
    'admin123', 
    '관리자', 
    'admin123', 
    '01012345678', 
    'ADMIN'
    );

-- 일반회원 계정 (TestDataInit.joinMember()에서 생성되는 데이터)
INSERT INTO member
    (
    member_id, birth, failed_count,
    created_date, expiration_date, last_modified_date,
    ttl, email, login_id, name, password, phone, member_grade
    )
VALUES
    (
        1002,
        '1995-05-15',
        0,
        CURRENT_TIMESTAMP
(), 
    DATEADD
('DAY', 14, CURRENT_TIMESTAMP
()), 
    CURRENT_TIMESTAMP
(),
    2592000, 
    'member@member.com', 
    'member123', 
    '일반회원', 
    'member123', 
    '01012345677', 
    'GUEST'
    );
ALTER TABLE member ALTER COLUMN member_id RESTART
WITH 1003;