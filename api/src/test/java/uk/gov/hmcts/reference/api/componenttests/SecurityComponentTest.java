package uk.gov.hmcts.reference.api.componenttests;

import org.junit.Test;

import static uk.gov.hmcts.reference.api.appeals.Appeal.AppealType.SOME_APPEAL;

public class SecurityComponentTest extends ComponentTestBase {

    @Test
    public void unauthenticatedUserShouldNotBeAllowed() throws Exception {
        scenario.given()
                .when().createAppeal("123", SOME_APPEAL, "Description")
                .then().forbidden();
    }

    @Test
    public void userShouldNotBeAbleToCreateAppealForAnotherUser() throws Exception {
        scenario.given().userId("123")
                .when().createAppeal("321", SOME_APPEAL, "Description")
                .then().forbidden();
    }

    @Test
    public void userShouldNotBeAbleToGetAppealOfAnotherUser() throws Exception {
        scenario.given().userId("123")
                .when().retrieveAppeal("321", "any")
                .then().forbidden();
    }
}
