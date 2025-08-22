import http from 'k6/http';
import { check, sleep } from 'k6';

// --- 테스트 환경 설정 ---
const BASE_URL = 'http://localhost:8080/api/v1';

// TestDataInitializer에서 생성한 데이터 범위
const USER_ID_MIN = 1;
const USER_ID_MAX = 100;
const BRAND_ID_MAX = 1000; // 상품이 최소 3000개 이상 생성됨

// --- 테스트 시나리오 옵션 ---
export const options = {
    stages: [
        // Start 100 iterations per `timeUnit` for the first 20 seconds.
        // 초기 부하: 첫 20초 동안 진행되며, 매 20초 동안 요청이 200개 만들어지므로 5 TPS
        { target: 50, duration: '20s' },

        // Linearly ramp-up to starting 8000 iterations per `timeUnit` over the following 1 minute.
        // 점진적 증가: 1분 동안 받는 요청을 10000 회로 증가시킴, 매 20초 동안 요청이 10000개 만들어지므로 500 TPS
        { target: 100, duration: '1m' },

        // Continue starting 8000 iterations per `timeUnit` for the following 1 minute.
        // 최대 부하 유지: 1분 동안 받는 요청이 10000으로 지속됨, 매 20초 동안 요청이 10000개 만들어지므로 500 TPS
        { target:100, duration: '1m' }, //pod 당 최대 400 tps(400 * 20s(timeUnit))

        // Linearly ramp-down to starting 60 iterations per timeUnit over the last 20 seconds.
        // 점진적 감소: 마지막 20초 동안 진행되며, 매 20초 동안 요청이 100개 만들어지므로 5 TPS
        { target: 0, duration: '30s' },
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
    //3. 상품 목록 조회 (가격순으로 조회)
    const page2 = Math.floor(Math.random() * 100) + 1;
    const resProducts2 = http.get(`${BASE_URL}/products/count?page=${page2}&size=30`, { headers });
    check(resProducts2, {
        '[Products] Status is 200': (r) => r.status === 200,
    });

    sleep(Math.random() * 2 + 1); // 1~3초간 상품 목록 구경 (Think Time)

}
