package com.lsh.mavikarga.config;


import com.lsh.mavikarga.config.oauth.PrincipalOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final PrincipalOAuth2UserService principalOAuth2UserService;

    @Autowired
    public SecurityConfig(PrincipalOAuth2UserService principalOAuth2UserService) {
        this.principalOAuth2UserService = principalOAuth2UserService;
    }



    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(request -> request
                        //.anyRequest().authenticated() // 인증만 되면 접근 가능한 경로

                        .requestMatchers("/", "/errors/**", "/info", "/clothing", "/login").permitAll() // 인증없이 접근 가능 경로
                        .requestMatchers("/payment/validate", "/payTest").permitAll() // 결재 시스템 테스트 중 ..
                        .requestMatchers("/js/**", "/css/**", "/bootstrap-5.3.2-dist/**", "/img/**", "/image/**", "/vid/**", "/*.ico", "/error").permitAll()

                        .requestMatchers("/bag")
                            .hasRole("USER") // "ROLE_USER" role 이 있어야 접근 가능한 경로 (자동 prefix: ROLE_)

                        .requestMatchers("/admins/**")
                            .hasRole("ADMIN") // "ROLE_ADMIN"

                        .anyRequest().authenticated() // 이외에는 모두 인증만 있으면 접근 가능
                )
                // OAuth2 로그인 처리
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/login") // 로그인 필요 경로 요청 시 보낼 경로(로그인 페이지)
                        .userInfoEndpoint(userInfo -> userInfo
                                // PrincipalOAuth2UserService extends DefaultOAuth2UserService 가 회원가입 처리함
                                .userService(principalOAuth2UserService))
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/"))
                .csrf(csrf -> csrf.disable());


        return http.build();
    }


    // 로그인 실패 핸들러
    @Bean
    AuthenticationFailureHandler authenticationFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler("/login?error=true");
    }


}
