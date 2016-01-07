package com.dsunny.engine;

import com.dsunny.entity.UserInfo;
import com.dsunny.utils.Utils;

import java.io.File;

/**
 * Created by user on 2016/1/4.
 */
public class AppUserManager {
    private static final long serialVersionUID = 1L;

    private static AppUserManager instance;

    private AppUserManager() {
    }

    /**
     * 获取实例
     */
    public static AppUserManager getInstance() {
        if (instance == null) {
            instance = new AppUserManager();
        }
        return instance;
    }

    /**
     * 将用户信息保存到本地Cache
     */
    public void saveUserInfo(final UserInfo userInfo) {
        Utils.SaveObject(AppConstants.USER_CACHE_PATH, userInfo);
    }

    /**
     * 从本地Cache中获取用户信息
     */
    public UserInfo restoreUserInfo() {
        Object object = Utils.restoreObject(AppConstants.USER_CACHE_PATH);
        return object == null ? null : (UserInfo) object;
    }

    /**
     * 重置用户信息
     */
    public void resetUserInfo(final UserInfo userInfo) {
        userInfo.setLoginName(null);
        userInfo.setUserName(null);
        userInfo.setScore(0);
        userInfo.setLoginStatus(false);
        saveUserInfo(userInfo);
    }

    /**
     * 删除本地缓存的用户信息
     */
    public void clearUserData() {
        File file = new File(AppConstants.USER_CACHE_PATH);
        File[] files = file.listFiles();
        if (files != null) {
            for (final File f : files) {
                f.delete();
            }
        }
    }

}
