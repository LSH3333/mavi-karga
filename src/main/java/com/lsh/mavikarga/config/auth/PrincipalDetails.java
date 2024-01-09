package com.lsh.mavikarga.config.auth;

import com.lsh.mavikarga.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * security 가 SecurityConfig 에 설정한 loginProcessingUrl 에 요청이 오면 필터로 낚아채서 로그인 진행시켜줌
 * 로그인 진행 완료되면 security session 을 만들어 Security ContextHolder 에 보관한다
 * 그런데 보관되는 오브젝트의 타입이 Authentication 타입이다
 * 그리고 Authentication 내부에 User 의 정보가 보관된다
 * User 의 타입은 UserDetails 타입으로 저장된다
 */
@Getter
public class PrincipalDetails implements UserDetails, OAuth2User {

    private User user;

    // OAuth2User 에 담겨있는 attributes
    private Map<String, Object> attributes;

    // OAuth2 로그인 생성자
    public PrincipalDetails(User user, Map<String,Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    // 해당 User 의 권한을 리턴
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> collect = new ArrayList<>();

        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });

        return collect;
    }


    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // 계정 만료 여부
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠금 여부
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 계정 비밀번호 만료 여부
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 활성화 여부
    @Override
    public boolean isEnabled() {
        return true;
    }


    // OAuth2User impl
    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public String getName() {
        return (String) this.attributes.get("sub");
    }
}
