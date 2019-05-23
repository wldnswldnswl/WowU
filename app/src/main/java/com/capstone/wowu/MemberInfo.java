package com.capstone.wowu;

public class MemberInfo {
    private String userEmail;
    private String userPwd;
    private String nickName;
    private String userName;
    private String phoneNum;

    public MemberInfo(String userEmail,String userPwd,String nickName,String userName,String phoneNum){
        this.userEmail=userEmail;
        this.userPwd=userPwd;
        this.nickName=nickName;
        this.userName=userName;
        this.phoneNum=phoneNum;
    }

    public String getUserEmail(){
        return this.userEmail;
    }
    public void setUserEmail(String userEmail){
        this.userEmail=userEmail;
    }

    public String getUserPwd(){
        return this.userPwd;
    }
    public void setUserPwd(String userPWd){
        this.userEmail=userPwd;
    }

    public String getNickName(){
        return this.nickName;
    }
    public void setNickName(String nickName){
        this.userEmail=nickName;
    }

    public String getUserName(){
        return this.userName;
    }
    public void setUserName(String userName){
        this.userEmail=userName;
    }

    public String getPhoneNum(){
        return this.phoneNum;
    }
    public void setPhoneNum(String phoneNum){
        this.phoneNum=phoneNum;
    }
}
