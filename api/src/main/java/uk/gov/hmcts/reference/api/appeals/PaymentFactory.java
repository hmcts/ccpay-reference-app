package uk.gov.hmcts.reference.api.appeals;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.payment.api.contract.PaymentDto;
import uk.gov.hmcts.reference.api.fees.FeesRegisterClient;
import uk.gov.hmcts.reference.api.payments.PaymentClient;

@Component
public class PaymentFactory implements Function<Appeal, Appeal.Payment> {
    private final FeesRegisterClient feesRegisterClient;
    private final PaymentClient paymentClient;
    private final String returnUrlTemplate;

    @Autowired
    public PaymentFactory(FeesRegisterClient feesRegisterClient,
                          PaymentClient paymentClient,
                          @Value("${payment.returnUrlTemplate}") String returnUrlTemplate) {
        this.feesRegisterClient = feesRegisterClient;
        this.paymentClient = paymentClient;
        this.returnUrlTemplate = returnUrlTemplate;
    }

    @Override
    public Appeal.Payment apply(Appeal appeal) {
        Integer amount = feesRegisterClient.retrieve(appeal.getType().getFeeCode());
        String returnUrl = String.format(returnUrlTemplate, appeal.getId());
        AtomicReference<PaymentDto> cachedPaymentHolder = new AtomicReference<>(paymentClient.create(amount, returnUrl));

        return new Appeal.Payment() {
            @Override
            public void refresh() {
                cachedPaymentHolder.set(paymentClient.retrieve(cachedPaymentHolder.get().getId()));
            }

            @Override
            public boolean isActive() {
                return !cachedPaymentHolder.get().getState().getFinished();
            }

            @Override
            public boolean isPaid() {
                return "success".equals(cachedPaymentHolder.get().getState().getStatus());
            }

            @Override
            public String getUrl() {
                return cachedPaymentHolder.get().getLinks().getNextUrl().getHref();
            }
        };
    }
}
