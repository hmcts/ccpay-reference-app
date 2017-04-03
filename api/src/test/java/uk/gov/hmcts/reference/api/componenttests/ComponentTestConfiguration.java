package uk.gov.hmcts.reference.api.componenttests;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.auth.checker.SubjectResolver;
import uk.gov.hmcts.auth.checker.User;
import uk.gov.hmcts.auth.provider.service.token.ServiceTokenGenerator;
import uk.gov.hmcts.reference.api.componenttests.backdoors.UserResolverBackdoor;

@Configuration
public class ComponentTestConfiguration {
    @Bean
    public SubjectResolver<User> userResolver() {
        return new UserResolverBackdoor();
    }

    @Bean
    public ServiceTokenGenerator serviceTokenGenerator() {
        return () -> "Bearer component-test-service-token";
    }

}
