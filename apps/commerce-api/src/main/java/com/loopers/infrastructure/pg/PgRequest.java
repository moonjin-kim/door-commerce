package com.loopers.infrastructure.pg;

import com.loopers.application.order.payment.PaymentCriteria;
import com.loopers.domain.pg.CardType;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

public class PgRequest {
    public record Pay(
            String orderId,
            CardType cardType,
            String cardNo,
            Long amount,
            String callbackUrl
    ) {
        public Pay(
                String orderId,
                CardType cardType,
                String cardNo,
                Long amount,
                String callbackUrl
        ) {
            if(orderId == null) {
                throw new CoreException(ErrorType.INVALID_INPUT, "주문 ID가 null입니다.");
            }

            if(cardType == null) {
                throw new CoreException(ErrorType.INVALID_INPUT, "카드 타입이 null입니다.");
            }

            if(cardNo == null) {
                throw new CoreException(ErrorType.INVALID_INPUT, "카드 번호가 null입니다.");
            }

            if(amount == null || amount <= 0) {
                throw new CoreException(ErrorType.INVALID_INPUT, "금액이 null이거나 0 이하입니다.");
            }

            if(callbackUrl == null || callbackUrl.isBlank()) {
                throw new CoreException(ErrorType.INVALID_INPUT, "콜백 URL이 null이거나 비어 있습니다.");
            }


            this.orderId = orderId;
            this.cardType = cardType;
            this.cardNo = cardNo;
            this.amount = amount;
            this.callbackUrl = callbackUrl;
        }
        public static Pay of(
                String orderId,
                CardType cardType,
                String cardNumber,
                Long amount,
                String callbackUrl
        ) {

            return new Pay(
                    orderId,
                    cardType,
                    cardNumber,
                    amount,
                    callbackUrl
            );
        }

        public static Pay from(PaymentCriteria.Pay command, String callbackUrl) {
            return new Pay(
                    String.valueOf(command.orderId()),
                    CardType.valueOf(command.cardType().name()),
                    command.cardNumber(),
                    command.amount(),
                    callbackUrl

            );
        }
    }
}
