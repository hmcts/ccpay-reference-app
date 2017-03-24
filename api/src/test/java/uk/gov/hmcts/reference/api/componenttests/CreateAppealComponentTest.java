package uk.gov.hmcts.reference.api.componenttests;

import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import uk.gov.hmcts.reference.api.appeals.AppealDto;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reference.api.appeals.Appeal.AppealStatus.AWAITING_PAYMENT;
import static uk.gov.hmcts.reference.api.appeals.Appeal.AppealType.SOME_APPEAL;

public class CreateAppealComponentTest extends ComponentTestBase {

    @Test
    public void createAppealShouldReturn200WithBody() throws Exception {
        scenario.given().userId("123")
                .when().createAppeal("123", SOME_APPEAL, "Description")
                .then().created((actual) -> {
                    assertThat(actual.getType()).isEqualTo(SOME_APPEAL);
                    assertThat(actual.getStatus()).isEqualTo(AWAITING_PAYMENT);
                    assertThat(actual.getDescription()).isEqualTo("Description");
                    assertThat(actual.getPaymentUrl()).isEqualTo("https://www.payments.service.gov.uk/secure/next-url");
                }
        );
    }

    @Test
    public void createdAppealShouldBeRetrievable() throws Exception {
        AtomicReference<AppealDto> createdHolder = new AtomicReference<>();

        scenario.given().userId("123")
                .when().createAppeal("123", SOME_APPEAL, "Description", createdHolder)
                .and().retrieveAppeal("123", createdHolder.get().getId().toString())
                .then().retrieved((actual) -> {
                    assertThat(actual.getType()).isEqualTo(SOME_APPEAL);
                    assertThat(actual.getStatus()).isEqualTo(AWAITING_PAYMENT);
                    assertThat(actual.getDescription()).isEqualTo("Description");
                    assertThat(actual.getPaymentUrl()).isEqualTo("https://www.payments.service.gov.uk/secure/next-url");
                }
        );
    }

}
