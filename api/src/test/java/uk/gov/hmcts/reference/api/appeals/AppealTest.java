package uk.gov.hmcts.reference.api.appeals;

import java.util.Optional;
import lombok.Data;
import org.junit.Test;
import uk.gov.hmcts.reference.api.appeals.Appeal.AppealStatus;
import uk.gov.hmcts.reference.api.appeals.Appeal.Payment;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reference.api.appeals.Appeal.AppealStatus.AWAITING_PAYMENT;
import static uk.gov.hmcts.reference.api.appeals.Appeal.AppealStatus.PAID;
import static uk.gov.hmcts.reference.api.appeals.Appeal.anAppealWith;

public class AppealTest {

    @Test
    public void paidAppealShouldNotHavePaymentUrl() {
        Appeal appeal = appealWithStatusAndPayment(PAID, null);
        assertThat(appeal.getPaymentUrl((any) -> null)).isEqualTo(Optional.empty());
    }

    @Test
    public void appealWithoutPaymentShouldCreateOne() {
        Appeal appeal = appealWithStatusAndPayment(AWAITING_PAYMENT, null);
        assertThat(appeal.getPaymentUrl((any) -> new PaymentStub(false, false, "url"))).isEqualTo(Optional.of("url"));
    }

    @Test
    public void appealWithPaidPaymentShouldChangeStatusToPaid() {
        Appeal appeal = appealWithStatusAndPayment(AWAITING_PAYMENT, new PaymentStub(false, true, null));
        assertThat(appeal.getPaymentUrl((any) -> null)).isEqualTo(Optional.empty());
        assertThat(appeal.getStatus()).isEqualTo(PAID);
    }

    @Test
    public void appealWithInactiveAndUnpaidPaymentShouldCreateANewOne() {
        Appeal appeal = appealWithStatusAndPayment(AWAITING_PAYMENT, new PaymentStub(false, false, "oldUrl"));
        assertThat(appeal.getPaymentUrl((any) ->  new PaymentStub(false, true, "newUrl"))).isEqualTo(Optional.of("newUrl"));
    }

    @Test
    public void appealWithActiveAndUnpaidPaymentShouldReuseAnOldOne() {
        Appeal appeal = appealWithStatusAndPayment(AWAITING_PAYMENT, new PaymentStub(true, false, "oldUrl"));
        assertThat(appeal.getPaymentUrl((any) ->  new PaymentStub(false, true, "newUrl"))).isEqualTo(Optional.of("oldUrl"));
    }

    private Appeal appealWithStatusAndPayment(AppealStatus status, Payment payment) {
        return anAppealWith().status(status).payment(payment).build();
    }

    @Data
    private static class PaymentStub implements Payment {
        private final boolean active;
        private final boolean paid;
        private final String url;

        @Override
        public void refresh() {
        }
    }
}