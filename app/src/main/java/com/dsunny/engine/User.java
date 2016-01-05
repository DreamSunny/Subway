package com.dsunny.engine;

import com.dsunny.entity.UserInfo;
import com.dsunny.utils.Utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Created by user on 2016/1/4.
 */
public class User implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;

    private static User instance;
    private UserInfo mUserInfo;

    private User() {
    }

    public static User getInstance() {
        if (instance == null) {
            // TODO
            instance = new User();
        }
        return instance;
    }

    public void save(){
        Utils.SaveObject(AppConstants.USER_CACHE_PATH, mUserInfo);
    }

    public void reset(){
        mUserInfo = null;
    }

    public User readResolve() throws ObjectStreamException, CloneNotSupportedException{
        instance = (User)this.clone();
        return instance;
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
