package com.capstone.wowu;

import com.google.firebase.database.IgnoreExtraProperties;

// [START blog_user_class]
@IgnoreExtraProperties
public class User {

    public String username;
    public String email;
    public String nickname;
    public String phone;
    public String image;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public User(String username, String email, String nickname, String phone, String image)
    {
        this.username = username;
        this.email = email;
        this.nickname = nickname;
        this.phone = phone;
        this.image = image;
    }

    public User(String username, String email, String nickname, String phone )
    {
        this.username = username;
        this.email = email;
        this.nickname = nickname;
        this.phone = phone;
    }

    public User(String email) {
        this.email = email;
    }

}
// [END blog_user_class]
