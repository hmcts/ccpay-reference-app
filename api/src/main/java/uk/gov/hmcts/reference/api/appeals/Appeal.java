package uk.gov.hmcts.reference.api.appeals;

import java.util.Optional;
import java.util.function.Function;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Wither;

import static lombok.AccessLevel.PRIVATE;
import static uk.gov.hmcts.reference.api.appeals.Appeal.AppealStatus.AWAITING_PAYMENT;
import static uk.gov.hmcts.reference.api.appeals.Appeal.AppealStatus.PAID;

@Data
@Wither
@Builder(builderMethodName = "anAppealWith")
public class Appeal {
    private final Integer id;
    private final AppealType type;
    private final String description;

    @Setter(value = PRIVATE)
    private AppealStatus status;

    @Getter(value = PRIVATE)
    @Setter(value = PRIVATE)
    private Payment payment;

    public Optional<String> getPaymentUrl(Function<Appeal, Payment> paymentFactory) {
        if (getStatus() != AWAITING_PAYMENT) {
            return Optional.empty();
        }

        if (payment == null) {
            payment = paymentFactory.apply(this);
        } else {
            payment.refresh();
            if (payment.isPaid()) {
                status = PAID;
                return Optional.empty();
            } else if (!payment.isActive()) {
                payment = paymentFactory.apply(this);
            }
        }

        return Optional.of(payment.getUrl());
    }

    public enum AppealType {
        HAPPY_PATH("X001"), WITH_UNCAUGHT_PAYMENT_REDIRECT("X002");

        private final String feeCode;

        AppealType(String feeCode) {
            this.feeCode = feeCode;
        }

        public String getFeeCode() {
            return feeCode;
        }
    }

    public enum AppealStatus {
        AWAITING_PAYMENT, PAID
    }

    public interface Payment {
        void refresh();

        boolean isActive();

        boolean isPaid();

        String getUrl();
    }
}
