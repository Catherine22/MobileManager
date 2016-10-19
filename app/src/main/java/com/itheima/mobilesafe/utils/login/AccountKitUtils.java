package com.itheima.mobilesafe.utils.login;

import android.app.Activity;
import android.content.Intent;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.itheima.mobilesafe.utils.Constants;

/**
 * Created by Catherine on 2016/10/19.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class AccountKitUtils implements BaseLogin {

    @Override
    public void login(Activity activity, Object TYPE) {
        final Intent intent = new Intent(activity, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        (LoginType) TYPE,
                        AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
        final AccountKitConfiguration configuration = configurationBuilder.build();
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configuration);
        activity.startActivityForResult(intent, Constants.ACCOUNT_KIT_REQ_CODE);
    }

    @Override
    public void logout() {
        AccountKit.logOut();
    }

    @Override
    public boolean isLogin() {
        AccessToken accessToken = AccountKit.getCurrentAccessToken();

        if (accessToken != null)
            return true;
        else
            return false;
    }
}
