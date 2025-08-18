package com.loopers.application.payment.pg;

public interface PgProcess {
    /**
     * 결제 요청을 처리합니다.
     *
     * @param command 결제 요청 정보
     * @return 결제 결과
     */
    PgResult.Pay payment(PgCommand.Pay command, Long userId);

    /**
     * 결제 취소를 처리합니다.
     *
     * @return 결제 취소 결과
     */
    PgResult.Find findByOrderId(String orderId);

    PgResult.Find findByPGId(String paymentId);
}
