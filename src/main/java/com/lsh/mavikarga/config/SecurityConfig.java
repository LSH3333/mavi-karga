package com.lsh.mavikarga.config;


import com.lsh.mavikarga.config.oauth.PrincipalOAuth2UserService;
import com.lsh.mavikarga.handler.CustomLoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final PrincipalOAuth2UserService principalOAuth2UserService;
    private final CustomLoginSuccessHandler customLoginSuccessHandler;

    @Autowired
    public SecurityConfig(PrincipalOAuth2UserService principalOAuth2UserService, CustomLoginSuccessHandler customLoginSuccessHandler) {
        this.principalOAuth2UserService = principalOAuth2UserService;
        this.customLoginSuccessHandler = customLoginSuccessHandler;
    }


    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {


        http
                .authorizeHttpRequests(request -> request
                                //.anyRequest().authenticated() // 인증만 되면 접근 가능한 경로

                                .requestMatchers("/", "/errors/**", "/info", "/login", "/locale", "/film", "/makingStory/**",
                                        "/policy/privacyPolicy", "/policy/termsOfService").permitAll() // 인증없이 접근 가능 경로
                                // Order
                                .requestMatchers("/clothing", "/clothing/**", "/order/products", "/order/products/add/nonuser", "/order/lookup",
                                        "/order/lookupSearch", "/order/products/cart", "/order/products/cart/remove", "/order/products/cart/count", "/order/products/cart/checkout").permitAll()
                                // 비회원 장바구니
                                .requestMatchers("/order/products/add/nonuser", "/order/cart", "/order/cart/nonuser", "/order/cart/nonuser/**", "/payments/payment/nonuser", "/payments/validate/nonuser").permitAll()

                                // 테스트 중 ..
                                .requestMatchers("/payment/validate", "/payTest", "/sliderTest", "/payments/**", "/test/**", "/carouselTest").permitAll()
                                // static files
                                .requestMatchers("/js/**", "/icon/**", "/css/**", "/bootstrap-5.3.2-dist/**", "/img/**", "/image/**", "/vid/**", "/*.ico", "/error").permitAll()

                                // "ROLE_USER" or "ROLE_ADMIN" role 이 있어야 접근 가능한 경로 (자동 prefix: ROLE_)
                                .requestMatchers("/bag", "/order/products/add", "/order/cart", "/users/**")
                                .hasAnyRole("USER", "ADMIN")

                                // 관리자 전용인데 개발용으로 열어놓음, 나중에 닫기
                                .requestMatchers("/admins/**", "/s3upload", "/s3render", "/s3uploadMany", "s3uploadAjax").permitAll()
//                        .requestMatchers("/admins/**")
//                            .hasRole("ADMIN") // "ROLE_ADMIN"


                                .anyRequest().authenticated() // 이외에는 모두 인증만 있으면 접근 가능
                )
                // OAuth2 로그인 처리
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/login") // 로그인 필요 경로 요청 시 보낼 경로(로그인 페이지)
                        .userInfoEndpoint(userInfo -> userInfo
                                // PrincipalOAuth2UserService extends DefaultOAuth2UserService 가 회원가입 처리함
                                .userService(principalOAuth2UserService))
                        .successHandler(customLoginSuccessHandler) // custom
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
