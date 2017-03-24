package uk.gov.hmcts.reference.api.configuration.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import uk.gov.hmcts.auth.checker.spring.useronly.AuthCheckerUserOnlyFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthCheckerUserOnlyFilter authCheckerFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        preserveUserTokenForPassThrough();

        authCheckerFilter.setAuthenticationManager(authenticationManager());

        http
                .addFilter(authCheckerFilter)
                .sessionManagement().sessionCreationPolicy(STATELESS).and()
                .csrf().disable()
                .formLogin().disable()
                .logout().disable()
                .authorizeRequests().anyRequest().authenticated();
    }

    /**
     * Don't erase user credentials as we pass them to the payments service.
     * Applications that do not call other authenticated services should omit this
     */
    private void preserveUserTokenForPassThrough() throws Exception {
        ProviderManager authenticationManager = (ProviderManager) authenticationManager();
        authenticationManager.setEraseCredentialsAfterAuthentication(false);
    }
}
