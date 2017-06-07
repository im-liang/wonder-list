package com.wonderlist.utility;

public class GoogleUserInfo {
    public String id = null;
    public String email = null;
    public boolean emailVerified = false;
    public String name = null;
    public String pictureUrl = null;
    public String locale = null;
    public String familyName = null;
    public String givenName = null;

    static GoogleUserInfo getDummyUser(){
        GoogleUserInfo result = new GoogleUserInfo();
        result.id = "321432";
        result.email = "foo@youfoo.fvckyou";
        result.emailVerified = true;
        result.name = "you idiot";
        result.pictureUrl = "www.your-fat-ass.com";
        result.locale = null;
        result.familyName = "your";
        result.givenName = "idiot";
        return  result;
    }
    static GoogleUserInfo getDummyUser1(){
        GoogleUserInfo result = new GoogleUserInfo();
        result.id = "3412513";
        result.email = "fo12o@youfoo.fvckyou";
        result.emailVerified = true;
        result.name = "you are idiot";
        result.pictureUrl = "www.your-fat-ass.com";
        result.locale = null;
        result.familyName = "your not";
        result.givenName = "idiot";
        return  result;
    }
}
