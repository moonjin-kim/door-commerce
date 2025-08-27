package com.loopers.application.payment;

import com.loopers.domain.PgInfo;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentEvent;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.pg.PgService;
import com.loopers.infrastructure.comman.CommonApplicationPublisher;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentFacade {
    private final PaymentService paymentService;
    private final PgService pgService;
    private final Map<String, PaymentMethod> paymentStrategyMap;

    public void requestPayment(PaymentCriteria.RequestPayment criteria) {
        PaymentMethod paymentMethod = paymentStrategyMap.get(criteria.method().name());
        if (paymentMethod == null) {
            throw new CoreException(ErrorType.UNSUPPORTED_PAYMENT_METHOD);
        }

        paymentMethod.pay(criteria);
    }

    @Transactional
    public void callback(PaymentCriteria.Callback criteria) {
        // 주문 조회
        Payment payment = paymentService.getPaymentByOrderId(criteria.orderId()).orElseThrow(
                () -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 결제: " + criteria.orderId() )
        );

        PgInfo.Find pgResult = pgService.findByTransactionKey(criteria.transactionKey(), payment.getUserId());
        if (pgResult == null) {
            paymentService.paymentFail(criteria.orderId(), "결제 정보가 없습니다.");
            return;
        } else if(!Objects.equals(pgResult.status(), "SUCCESS")) {
            paymentService.paymentFail(criteria.orderId(), pgResult.reason());
            return;
        }

        // 결제 완료 저장
        paymentService.paymentComplete(payment.getOrderId(), pgResult.transactionKey());
    }

    public void syncPayment(LocalDateTime currentTime) {
        // 주문 정보 조회
        List<Payment> payments = paymentService.getPendingOrders();

        for(Payment payment : payments) {
            try {
                PgInfo.FindByOrderId pgResults = pgService.findByOrderId(payment.getOrderId(), payment.getUserId());

                boolean isNotPaid = pgResults.transactions().isEmpty();
                String reason = "주문 정보가 없습니다.";
                for(PgInfo.Transactional pgResult : pgResults.transactions()) {
                    isNotPaid = true;
                    if(pgResult.status().equals("SUCCESS")) {
                        // 결제 정보가 있는 경우 주문 만료 처리
                        paymentService.paymentComplete(payment.getOrderId(), pgResult.transactionKey());
                        isNotPaid = false;
                        break;
                    }
                }

                if(isNotPaid) {
                    // 재고 복구
                    paymentService.paymentFail(payment.getOrderId(), reason);
                }
            } catch (Exception e) {
                log.error("주문 동기화 중 오류 발생: {}", payment.getOrderId(), e);
            }
        }
    }
}
