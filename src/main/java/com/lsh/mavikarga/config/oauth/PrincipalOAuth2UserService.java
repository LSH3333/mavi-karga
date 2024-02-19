package com.lsh.mavikarga.config.oauth;


import com.lsh.mavikarga.config.auth.PrincipalDetails;
import com.lsh.mavikarga.config.oauth.provider.GoogleUserInfo;
import com.lsh.mavikarga.config.oauth.provider.KakaoUserInfo;
import com.lsh.mavikarga.config.oauth.provider.NaverUserInfo;
import com.lsh.mavikarga.config.oauth.provider.OAuth2UserInfo;
import com.lsh.mavikarga.domain.User;
import com.lsh.mavikarga.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
//@Transactional
public class PrincipalOAuth2UserService extends DefaultOAuth2UserService {


    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Autowired
    public PrincipalOAuth2UserService(PasswordEncoder passwordEncoder, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    // spring security 는 auth_code 를 리소스 서버에서 받아서, access_token 을 요청하고 받는 과정을 스스로 처리한다
    // 즉 우리는 access_token 을 알 필요도 없다..

    // OAuth2UserRequest 에 이미 access_token 담겨 있다
    // 사용자가 구글 로그인 -> 리소스 서버가 auth_code 클라이언트에 리턴 및 리다이렉트 -> 발급 받은 auth_code 리소스 서버에 보내 access_token 요청 -> 리소스서버는 access_token 클라이언트에 발급
    // 여기 까지의 정보가 OAuth2UserRequest 에 담겨있고, 이제 이걸 이용해서 필요한 회원 정보들을 리소스 서버에 요청 가능
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("userRequest = {}", userRequest);
        // ClientRegistration{registrationId='google', clientId='', clientSecret='', clientAuthenticationMethod=client_secret_basic, authorizationGrantType=org.springframework.security.oauth2.core.AuthorizationGrantType@5da5e9f3, redirectUri='{baseUrl}/{action}/oauth2/code/{registrationId}', scopes=[profile, email], providerDetails=org.springframework.security.oauth2.client.registration.ClientRegistration$ProviderDetails@1eac847c, clientName='Google'}
        log.info("userRequest.getClientRegistration() = {}", userRequest.getClientRegistration());
        log.info("userRequest.getAccessToken().getTokenValue() = {}", userRequest.getAccessToken().getTokenValue());
        log.info("userRequest.getClientRegistration() = {}", userRequest.getClientRegistration());


        OAuth2User oAuth2User = super.loadUser(userRequest);
        // {sub=113278514104204816500, name=이세현, given_name=세현, family_name=이, picture=https://lh3.googleusercontent.com/a/ACg8ocLjmyFdD4xwZx25hfhq4DEzJ7HpOiEH11PvmGg6RD-c=s96-c, email=dltpgustpgus@gmail.com, email_verified=true, locale=ko}
        log.info("super.loadUser(userRequest).getAttributes() = {}", oAuth2User.getAttributes());

        OAuth2UserInfo oAuth2UserInfo = null;
        // GOOGLE
        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        }
        // NAVER
        else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            // naver 가 리턴 해주는 json
            // oAuth2User.getAttributes() = {resultcode=00, message=success, response={id=Espin_Vgi-JRn4SxQzLlTDg1Pz58s-DL3ZXN1GkGphQ, email=chadol51@naver.com, name=이세현}}
            oAuth2UserInfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response"));
        }
        // KAKAO
        else if (userRequest.getClientRegistration().getRegistrationId().equals("kakao")) {
            // oAuth2User.getAttributes() = {id=3233146583, connected_at=2023-12-20T04:09:49Z, properties={nickname=이세현}, kakao_account={profile_nickname_needs_agreement=false, profile={nickname=이세현}}}
            oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());

        } else {
            log.info("지원하지 않는 provider");
            throw new OAuth2AuthenticationException("지원하지 않는 provider");
        }


        // 회원가입
        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String email = oAuth2UserInfo.getEmail();
        String username = provider + "_" + providerId; // google_45312134...
        String password = passwordEncoder.encode("password");
        String role = "ROLE_USER";
        // 회원 중복 확인
        User userEntity = userService.findByUsername(username).orElse(null);
        // 중복 아니면 새로 생성후 저장
        if (userEntity == null) {
//            userEntity = new User(username, password, email, role, provider, providerId, LocalDateTime.now());
            userEntity = new User(username, password, role, provider, providerId, LocalDateTime.now());
            userService.save(userEntity);
        }
        // Authentication 에 담기게됨
        // PrincipalDetails implements UserDetails, OAuth2User
        // UserDetails(일반회원), OAuth2User(OAuth2회원) 를 상속 받는 PrincipalDetails 타입으로 Authentication 저장해서 두 종류 모두 동일하게 처리할수 있도록함
        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }
}
