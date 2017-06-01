package uk.gov.hmcts.reference.api.componenttests;

import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import uk.gov.hmcts.reference.api.appeals.AppealDtos.AppealDto;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reference.api.appeals.Appeal.AppealStatus.AWAITING_PAYMENT;
import static uk.gov.hmcts.reference.api.appeals.Appeal.AppealType.HAPPY_PATH;
import static uk.gov.hmcts.reference.api.appeals.AppealDtos.AppealListItemDto;

public class CreateAppealComponentTest extends ComponentTestBase {

    @Test
    public void createAppealShouldWork() throws Exception {
        scenario.given().userId("1")
            .when().createAppeal("1", HAPPY_PATH, "Description")
            .then().created((actual) -> {
                assertThat(actual.getType()).isEqualTo(HAPPY_PATH);
                assertThat(actual.getStatus()).isEqualTo(AWAITING_PAYMENT);
                assertThat(actual.getDescription()).isEqualTo("Description");
                assertThat(actual.getPaymentUrl()).isEqualTo("https://www.payments.service.gov.uk/secure/just-created-url");
            }
        );
    }

    @Test
    public void createdAppealShouldBeRetrievable() throws Exception {
        AtomicReference<AppealDto> createdHolder = new AtomicReference<>();

        scenario.given().userId("1")
            .when().createAppeal("1", HAPPY_PATH, "Description", createdHolder)
            .and().retrieveAppeal("1", createdHolder.get().getId())
            .then().retrieved((actual) -> {
                assertThat(actual.getType()).isEqualTo(HAPPY_PATH);
                assertThat(actual.getStatus()).isEqualTo(AWAITING_PAYMENT);
                assertThat(actual.getDescription()).isEqualTo("Description");
                assertThat(actual.getPaymentUrl()).isEqualTo("https://www.payments.service.gov.uk/secure/retrieved-url");
            }
        );
    }

    @Test
    public void createdAppealShouldBeAppearInTheList() throws Exception {
        AtomicReference<AppealDto> createdHolder = new AtomicReference<>();

        scenario.given().userId("1")
            .when().createAppeal("1", HAPPY_PATH, "Description", createdHolder)
            .and().retrieveAppealList("1")
            .then().retrievedList((actual) -> {
                assertThat(actual).contains(new AppealListItemDto(createdHolder.get().getId(), HAPPY_PATH, AWAITING_PAYMENT, "Description"));
            }
        );
    }
}
