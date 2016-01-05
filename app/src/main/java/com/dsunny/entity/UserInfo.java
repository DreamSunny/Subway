package com.dsunny.entity;

import java.io.Serializable;

/**
 * Created by user on 2016/1/5.
 */
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String loginName;
    private String userName;
    private int score;
    private boolean loginStatus;

    public UserInfo() {
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(boolean loginStatus) {
        this.loginStatus = loginStatus;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "loginName='" + loginName + '\'' +
                ", userName='" + userName + '\'' +
                ", score=" + score +
                ", loginStatus=" + loginStatus +
                '}';
    }
}
