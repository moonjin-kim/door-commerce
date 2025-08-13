-- 재귀 쿼리의 최대 깊이를 1,000만 이상으로 넉넉하게 설정합니다.
SET @@cte_max_recursion_depth = 1100000;

-- 1. 브랜드 1,000개 생성 (기존 30개에서 증가)
INSERT INTO brand (name, description, logo_url, status, created_at, updated_at)
WITH RECURSIVE numbers AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM numbers WHERE n < 1000 -- 1,000개로 수정
)
SELECT
    CONCAT('테스트 브랜드 ', n),
    CONCAT('테스트를 위한 브랜드입니다. No.', n),
    CONCAT('https://test.com/logo-', n, '.png'),
    'ACTIVE',
    NOW(),
    NOW()
FROM numbers;

-- 2. 유저 10,000명 생성 (기존 100명에서 증가)
INSERT INTO member (name, account, email, birthday, gender, created_at, updated_at)
WITH RECURSIVE numbers AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM numbers WHERE n < 1000 -- 10,000명으로 수정
)
SELECT
    CONCAT('테스트유저', n),
    CONCAT('testuser', n),
    CONCAT('test', n, '@email.com'),
    '2000-01-01',
    'MALE',
    NOW(),
    NOW()
FROM numbers;

-- 3. 유저별 포인트 생성 (10,000명)
INSERT INTO point (user_id, balance, version, created_at, updated_at)
WITH RECURSIVE numbers AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM numbers WHERE n < 1000 -- 10,000명으로 수정
)
SELECT
    n,
    1000000,
    0,
    NOW(),
    NOW()
FROM numbers;


-- 4. 상품 1,000만 개 생성 (30개 브랜드에 랜덤 할당)
INSERT INTO product (brand_id, name, description, image_url, price, status, like_count, created_at, updated_at)
WITH RECURSIVE numbers AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM numbers WHERE n < 1000000 -- 1,000만 개로 수정
)
SELECT
    FLOOR(1 + RAND() * 1000) AS brand_id, -- 1부터 1,000 사이의 랜덤 브랜드 ID (중요: 함께 수정)
    CONCAT('랜덤 상품 ', n),
    CONCAT('랜덤 상품 설명 ', n),
    CONCAT('https://test.com/product-', n, '.png'),
    FLOOR(100 + RAND() * 1901) * 100 AS price,
    'SALE' as status,
    FLOOR(RAND() * 100000) as like_count,
    NOW(),
    NOW()
FROM numbers;

-- 5. 생성된 상품에 대한 재고 1,000만 개 생성 (각 100개씩)
INSERT INTO stock (product_id, quantity, created_at, updated_at)
WITH RECURSIVE numbers AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM numbers WHERE n < 1000000 -- 1,000만 개로 수정
)
SELECT
    n AS product_id,
    100 AS quantity,
    NOW(),
    NOW()
FROM numbers;


SET @@cte_max_recursion_depth = 5100000;

-- 총 5천만 개의 랜덤 '좋아요' 데이터를 생성합니다.
INSERT IGNORE INTO likes (user_id, product_id, created_at)
WITH RECURSIVE numbers AS (
    SELECT 1 AS n
    UNION ALL
    -- 여기에 생성하고 싶은 '좋아요' 총 개수를 입력하세요 (현재 5천만 개)
    SELECT n + 1 FROM numbers WHERE n < 5000000
)
SELECT
    -- 1 ~ 10,000 사이의 랜덤 유저 ID 생성
    FLOOR(1 + RAND() * 10000) AS member_id,
    -- 1 ~ 1,000만 사이의 랜덤 상품 ID 생성
    FLOOR(1 + RAND() * 1000000) AS product_id,
    NOW()
FROM numbers;
