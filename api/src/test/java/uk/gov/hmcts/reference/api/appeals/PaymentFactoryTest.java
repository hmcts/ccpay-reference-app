package uk.gov.hmcts.reference.api.appeals;

import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.hmcts.payment.api.contract.PaymentDto;
import uk.gov.hmcts.reference.api.appeals.Appeal.Payment;
import uk.gov.hmcts.reference.api.fees.FeesRegisterClient;
import uk.gov.hmcts.reference.api.payments.PaymentClient;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static uk.gov.hmcts.payment.api.contract.PaymentDto.paymentDtoWith;
import static uk.gov.hmcts.reference.api.appeals.Appeal.AppealType.HAPPY_PATH;
import static uk.gov.hmcts.reference.api.appeals.Appeal.anAppealWith;

public class PaymentFactoryTest {
    private static final PaymentDto VALID_PAYMENT_DTO = paymentDtoWith()
            .id("456")
            .state(new PaymentDto.StateDto("cancelled", true))
            .links(new PaymentDto.LinksDto(new PaymentDto.LinkDto("next-url", "GET"), null))
            .build();

    private final FeesRegisterClient feesRegisterClient = Mockito.mock(FeesRegisterClient.class);
    private final PaymentClient paymentClient = Mockito.mock(PaymentClient.class);
    private final String urlTemplate = "return-to-%s-url";
    private final PaymentFactory factory = new PaymentFactory(feesRegisterClient, paymentClient, urlTemplate);

    @Test
    public void amountShouldBeBasedOnFeesRegisterAndReturnUrlOnTemplate() {
        when(feesRegisterClient.retrieve(HAPPY_PATH.getFeeCode())).thenReturn(999);
        factory.apply(anAppealWith().id(123).type(HAPPY_PATH).build());
        verify(paymentClient).create(999, "return-to-123-url");
    }

    @Test
    public void onlyRefreshShouldTriggerRetrieve() {
        when(paymentClient.create(any(), any())).thenReturn(VALID_PAYMENT_DTO);
        when(paymentClient.retrieve(VALID_PAYMENT_DTO.getId())).thenReturn(VALID_PAYMENT_DTO);
        Payment payment = factory.apply(anAppealWith().id(123).type(HAPPY_PATH).build());

        verify(paymentClient, times(0)).retrieve(VALID_PAYMENT_DTO.getId());
        payment.refresh();
        verify(paymentClient, times(1)).retrieve(VALID_PAYMENT_DTO.getId());
        payment.isActive();
        payment.isPaid();
        payment.getUrl();
        verify(paymentClient, times(1)).retrieve(VALID_PAYMENT_DTO.getId());
    }
}