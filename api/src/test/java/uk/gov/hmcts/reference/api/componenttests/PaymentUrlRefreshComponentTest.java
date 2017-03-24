package uk.gov.hmcts.reference.api.componenttests;

import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import uk.gov.hmcts.reference.api.appeals.AppealDtos.AppealDto;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reference.api.appeals.Appeal.AppealType.SOME_TYPE;

public class PaymentUrlRefreshComponentTest extends ComponentTestBase {

    private final AtomicReference<AppealDto> createdHolder = new AtomicReference<>();

    @Test
    public void activePaymentUrlShouldBeReused() throws Exception {
        scenario.given().userId("1")
                .when().createAppeal("1", SOME_TYPE, "Description", createdHolder)
                .and().retrieveAppeal("1", createdHolder.get().getId())
                .then().created((actual) -> assertThat(actual.getPaymentUrl()).isEqualTo("https://www.payments.service.gov.uk/secure/retrieved-url")
        );
    }

    @Test
    public void inactivePaymentUrlShouldBeRecreated() throws Exception {
        scenario.given().userId("2")
                .when().createAppeal("2", SOME_TYPE, "Description", createdHolder)
                .and().retrieveAppeal("2", createdHolder.get().getId())
                .then().created((actual) -> assertThat(actual.getPaymentUrl()).isEqualTo("https://www.payments.service.gov.uk/secure/just-created-url")
        );
    }

    @Test
    public void paidAppealShouldNotHavePaymentUrl() throws Exception {
        scenario.given().userId("3")
                .when().createAppeal("3", SOME_TYPE, "Description", createdHolder)
                .and().retrieveAppeal("3", createdHolder.get().getId())
                .then().created((actual) -> assertThat(actual.getPaymentUrl()).isNull()
        );
    }
}
