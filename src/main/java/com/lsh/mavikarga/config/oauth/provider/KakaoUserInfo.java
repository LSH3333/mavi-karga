package com.lsh.mavikarga.config.oauth.provider;

import java.util.Map;

public class KakaoUserInfo implements OAuth2UserInfo {

    // OAuth2User.getAttributes()
    private Map<String,Object> attributes;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        Long id_long = (Long) attributes.get("id");
        return id_long.toString();
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    // kakao 는 email 안 받음, 만약 email 필요하다면 nickname 으로 대체
    @Override
    public String getEmail() {
        return getName();
    }

    @Override
    public String getName() {
        Map properties = (Map) attributes.get("properties");
        return (String) properties.get("nickname");
    }
}