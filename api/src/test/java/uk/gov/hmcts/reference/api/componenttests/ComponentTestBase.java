package uk.gov.hmcts.reference.api.componenttests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.reference.api.componenttests.backdoors.UserResolverBackdoor;
import uk.gov.hmcts.reference.api.componenttests.dsl.AppealTestDsl;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@ActiveProfiles({"componenttest"})
@SpringBootTest(webEnvironment = MOCK)
public class ComponentTestBase {

    @ClassRule
    public static WireMockRule wireMockRule = new WireMockRule(19782);

    @Autowired
    protected UserResolverBackdoor userRequestAuthorizer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    AppealTestDsl scenario;

    @Before
    public void setUp() {
        MockMvc mvc = webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        this.scenario = new AppealTestDsl(mvc, userRequestAuthorizer, objectMapper);
    }
}
