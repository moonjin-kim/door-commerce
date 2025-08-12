import http from 'k6/http';
import { check, sleep } from 'k6';

// --- 테스트 환경 설정 ---
const BASE_URL = 'http://localhost:8080/api/v1';

// TestDataInitializer에서 생성한 데이터 범위
const USER_ID_MIN = 1;
const USER_ID_MAX = 100;
const PRODUCT_ID_MIN = 1;
const PRODUCT_ID_MAX = 10000; // 상품이 최소 3000개 이상 생성됨
const BRAND_ID_MAX = 100; // 상품이 최소 3000개 이상 생성됨

// --- 테스트 시나리오 옵션 ---
export const options = {
    stages: [
        { duration: '30s', target: 10 },  // 30초에 걸쳐 사용자를 50명까지 늘립니다 (Ramp-up)
        { duration: '1m', target: 10 },   // 1분 동안 사용자 50명을 유지하며 부하를 가합니다 (Steady State)
        { duration: '30s', target: 0 },   // 30초에 걸쳐 사용자를 0명으로 줄입니다 (Ramp-down)
    ],
    thresholds: {
        // 테스트 성공/실패 기준 정의
        'http_req_failed': ['rate<0.01'],      // HTTP 에러율이 1% 미만이어야 함
        'http_req_duration': ['p(95)<800'], // 전체 요청의 95%가 800ms 안에 처리되어야 함
        'checks': ['rate>0.99'],              // 모든 check의 성공률이 99% 이상이어야 함
    },
};

// --- 가상 사용자(VU)가 실행할 테스트 로직 ---
export default function () {
    // 1. 각 VU에게 무작위 사용자 ID 할당
    const userId = Math.floor(Math.random() * (USER_ID_MAX - USER_ID_MIN + 1)) + USER_ID_MIN;
    const headers = {
        'Content-Type': 'application/json',
        'X-USER-ID': `${userId}`,
    };

    // 2. 상품 목록 조회 (랜덤 페이지)
    const page = Math.floor(Math.random() * 100) + 1;
    const resProducts = http.get(`${BASE_URL}/products?page=${page}&size=20`, { headers });
    check(resProducts, {
        '[Products] Status is 200': (r) => r.status === 200,
    });

    sleep(Math.random() * 2 + 1); // 1~3초간 상품 목록 구경 (Think Time)

    // 3. 상품 목록 조회 (가격순으로 조회)
    const page2 = Math.floor(Math.random() * 100) + 1;
    const resProducts2 = http.get(`${BASE_URL}/products?page=${page}&size=20?sort=price_asc`, { headers });
    check(resProducts, {
        '[Products] Status is 200': (r) => r.status === 200,
    });

    sleep(Math.random() * 2 + 1); // 1~3초간 상품 목록 구경 (Think Time)

    // 4. 상품 목록 조회 (좋아요 순으로 정렬)
    const page3 = Math.floor(Math.random() * 10) + 1;
    const resProducts3 = http.get(`${BASE_URL}/products?page=${page}&size=20?sort=like_desc`, { headers });
    check(resProducts, {
        '[Products] Status is 200': (r) => r.status === 200,
    });

    sleep(Math.random() * 2 + 1); // 1~3초간 상품 목록 구경 (Think Time)

    // 3. 상품 목록 조회 브랜드 id로 조회
    const page4 = Math.floor(Math.random() * 10) + 1;
    const brandId = Math.floor(Math.random() * BRAND_ID_MAX) + 1; // 1~100 사이의 브랜드 ID
    const resProducts4 = http.get(`${BASE_URL}/products?page=${page}&size=20?sort=like_desc?brandId=${brandId}`, { headers });
    check(resProducts, {
        '[Products] Status is 200': (r) => r.status === 200,
    });

    sleep(Math.random() * 2 + 1); // 1~3초간 상품 목록 구경 (Think Time)

}
