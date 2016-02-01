/**
 * GRUPO RAIDO CONFIDENTIAL
 * __________________
 *
 * [2015] - [2015] Grupo Raido SAPI de CV
 * All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains
 * the property of Grupo Raido SAPI de CV and its suppliers,
 * if any. The intellectual and technical concepts contained
 * herein are proprietary to Grupo Raido SAPI de CV and its
 * suppliers and may be covered by México and Foreign Patents,
 * patents in process, and are protected by trade secret or
 * copyright law. Dissemination of this information or
 * reproduction of this material is strictly forbidden unless
 * prior written permission is obtained from Grupo Raido SAPI
 * de CV.
 */

package com.gruporaido.tasker_partner.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.gruporaido.tasker_library.R;
import com.gruporaido.tasker_library.activity.SingleFragmentActivity;
import com.gruporaido.tasker_library.communication.TargetListener;
import com.gruporaido.tasker_library.report.Logging;
import com.gruporaido.tasker_library.util.ApplicationComponent;
import com.gruporaido.tasker_partner.fragment.LoginFragment;
import com.gruporaido.tasker_partner.util.TaskerPartnerComponent;

import java.lang.Override;
import java.lang.String;

public class LoginActivity extends SingleFragmentActivity implements TargetListener {

    public static final int REQUEST_LOGIN = 0;

    @Override
    protected Fragment createFragment() {
        LoginFragment fragment = new LoginFragment();
        fragment.setTargetListener(this, REQUEST_LOGIN);
        return fragment;
    }

    @Override
    public void injectActivity(ApplicationComponent component) {
        ((TaskerPartnerComponent) component).inject(this);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logging.deleteLog(this);
    }

    @Override
    public void onResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode != TargetListener.RESULT_OK) return;

        switch (requestCode) {
            case REQUEST_LOGIN:
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
        }
    }

}
