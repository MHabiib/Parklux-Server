package com.future.pms.model;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.io.Serializable;

@Document @Getter @Setter @ToString @EqualsAndHashCode() public class MongoRefreshToken
    implements Serializable {

    public static final String TOKEN_ID = "tokenId";

    private String tokenId;
    private OAuth2RefreshToken token;
    private String authentication;

    public OAuth2Authentication getAuthentication() {
        return SerializableObjectConverter.deserialize(authentication);
    }

    public void setAuthentication(OAuth2Authentication authentication) {
        this.authentication = SerializableObjectConverter.serialize(authentication);
    }
}
