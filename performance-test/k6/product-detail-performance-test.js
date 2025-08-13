import http from 'performance-test/k6/http';
import { check, sleep } from 'k6';

// --- 테스트 환경 설정 ---
const BASE_URL = 'http://localhost:8080/api/v1';

// TestDataInitializer에서 생성한 데이터 범위
const USER_ID_MIN = 1;
const USER_ID_MAX = 100;
const PRODUCT_ID_MIN = 1;
const PRODUCT_ID_MAX = 10000; // 상품이 최소 3000개 이상 생성됨

// --- 테스트 시나리오 옵션 ---
export const options = {
    stages: [
        { duration: '30s', target: 50 },  // 30초에 걸쳐 사용자를 50명까지 늘립니다 (Ramp-up)
        { duration: '1m', target: 50 },   // 1분 동안 사용자 50명을 유지하며 부하를 가합니다 (Steady State)
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

    // // 3. 상품 상세 조회 (랜덤 상품)
    const productId = Math.floor(Math.random() * (PRODUCT_ID_MAX - PRODUCT_ID_MIN + 1)) + PRODUCT_ID_MIN;
    const resProductDetail = http.get(`${BASE_URL}/products/${productId}`, { headers });
    check(resProductDetail, {
        '[Product Detail] Status is 200': (r) => r.status === 200,
    });
    //
    // sleep(Math.random() * 2 + 1); // 1~3초간 상품 상세 정보 확인
    //
    // sleep(3); // 주문 완료 후 대기
}
