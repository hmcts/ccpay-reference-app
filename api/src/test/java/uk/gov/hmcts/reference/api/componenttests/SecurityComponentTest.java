package uk.gov.hmcts.reference.api.componenttests;

import org.junit.Test;

import static uk.gov.hmcts.reference.api.appeals.Appeal.AppealType.SOME_APPEAL;

public class SecurityComponentTest extends ComponentTestBase {

    @Test
    public void unauthenticatedUserShouldNotBeAllowed() throws Exception {
        scenario.given()
                .when().createAppeal("1", SOME_APPEAL, "Description")
                .then().forbidden();
    }

    @Test
    public void userShouldNotBeAbleToCreateAppealForAnotherUser() throws Exception {
        scenario.given().userId("1")
                .when().createAppeal("2", SOME_APPEAL, "Description")
                .then().forbidden();
    }

    @Test
    public void userShouldNotBeAbleToGetAppealOfAnotherUser() throws Exception {
        scenario.given().userId("1")
                .when().retrieveAppeal("2", "any")
                .then().forbidden();
    }
}
