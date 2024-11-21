package uk.gov.hmcts.dts.fact.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@SuppressWarnings("PMD.SignatureDeclareThrowsException")
@Configuration
public class SecurityConfiguration {

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.GET, "/admin/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/courts/").authenticated()
                .requestMatchers(HttpMethod.GET, "/courts/all").authenticated()
                .requestMatchers(HttpMethod.GET, "/courts/{slug}/courtPhoto").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/*").authenticated()
                .requestMatchers(HttpMethod.POST, "/*").authenticated()
                .requestMatchers(HttpMethod.PUT, "/*").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/*").authenticated()
                .requestMatchers(HttpMethod.POST, "/*").authenticated()
                .anyRequest().permitAll()
            ).oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())).csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public RolesProvider rolesProvider() {
        return new SpringRolesProvider();
    }

}
