package com.wonderlist.utility;

import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.jackson.JacksonFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;

public class GoogleAuth {
    public static final String DUMMY_USER_TOKEN = "asdfghjkl";
    public static final String DUMMY_USER_TOKEN1 = "123";
    public static GoogleUserInfo googleVerification(String token){
        if(Objects.equals(token, DUMMY_USER_TOKEN)){
            return GoogleUserInfo.getDummyUser();
        }
        if(Objects.equals(token, DUMMY_USER_TOKEN1)){
            return GoogleUserInfo.getDummyUser1();
        }
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier(UrlFetchTransport.getDefaultInstance(), new JacksonFactory());
        try {
            GoogleIdToken idToken = verifier.verify(token);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                GoogleUserInfo user = new GoogleUserInfo();
                user.id = payload.getSubject();
                user.email = payload.getEmail();
                user.emailVerified = payload.getEmailVerified();
                user.name = (String) payload.get("name");
                user.pictureUrl = (String) payload.get("picture");
                user.locale = (String) payload.get("locale");
                user.familyName = (String) payload.get("family_name");
                user.givenName = (String) payload.get("given_name");
                return user;
            } else {
                return null;
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
