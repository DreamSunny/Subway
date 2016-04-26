package com.dsunny.engine;

import com.dsunny.common.AppConstants;
import com.dsunny.network.entity.UserInfo;
import com.dsunny.util.AppUtil;

import java.io.File;

/**
 * 本地用户信息管理类
 */
public class AppUserManager {

    private AppUserManager() {
    }

    /**
     * 获取本地用户信息管理类实例
     *
     * @return 本地用户信息管理类的实例
     */
    public static AppUserManager getInstance() {
        return AppUserManagerHolder.INSTANCE;
    }

    /**
     * 内部类实现单例
     */
    private static class AppUserManagerHolder {
        private static final AppUserManager INSTANCE = new AppUserManager();
    }

    /**
     * 将用户信息保存到本地Cache
     *
     * @param userInfo 用户信息
     */
    public void saveUserInfo(final UserInfo userInfo) {
        AppUtil.SaveObject(AppConstants.USER_CACHE_PATH, userInfo);
    }

    /**
     * 从本地Cache中获取用户信息
     *
     * @return 用户信息
     */
    public UserInfo restoreUserInfo() {
        Object object = AppUtil.RestoreObject(AppConstants.USER_CACHE_PATH);
        return object == null ? null : (UserInfo) object;
    }

    /**
     * 重置用户信息
     *
     * @param userInfo 用户信息
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
