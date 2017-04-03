package uk.gov.hmcts.reference.api.configuration.security;


import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import uk.gov.hmcts.auth.checker.RequestAuthorizer;
import uk.gov.hmcts.auth.checker.User;
import uk.gov.hmcts.auth.checker.spring.useronly.AuthCheckerUserOnlyFilter;

@Configuration
public class AuthCheckerConfiguration {

    @Bean
    public Function<HttpServletRequest, Optional<String>> userIdExtractor() {
        Pattern pattern = Pattern.compile("^/users/([^/]+)/.+$");

        return (request) -> {
            Matcher matcher = pattern.matcher(request.getRequestURI());
            boolean matched = matcher.find();
            return Optional.ofNullable(matched ? matcher.group(1) : null);
        };
    }

    @Bean
    public Function<HttpServletRequest, Collection<String>> authorizedRolesExtractor() {
        return (any) -> Collections.singletonList("citizen");
    }

    @Bean
    public AuthCheckerUserOnlyFilter authCheckerUserOnlyFilter(RequestAuthorizer<User> userRequestAuthorizer,
                                                               AuthenticationManager authenticationManager) {
        AuthCheckerUserOnlyFilter filter = new AuthCheckerUserOnlyFilter(userRequestAuthorizer);
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }
}
