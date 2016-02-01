/**
 *
 * GRUPO RAIDO CONFIDENTIAL
 * __________________
 *
 *  [2015] - [2015] Grupo Raido SAPI de CV
 *  All Rights Reserved.
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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.gruporaido.tasker_library.activity.SingleFragmentActivity;
import com.gruporaido.tasker_library.communication.TargetListener;
import com.gruporaido.tasker_partner.R;
import com.gruporaido.tasker_partner.fragment.BankAccountFragment;
import com.gruporaido.tasker_library.model.BankAccount;
import com.gruporaido.tasker_partner.util.APIFetch;
import com.gruporaido.tasker_partner.util.APIResponseHandler;
import com.gruporaido.tasker_partner.util.ErrorHandler;
import com.gruporaido.tasker_library.activity.DaggerActivity;
import com.gruporaido.tasker_library.util.ApplicationComponent;
import com.gruporaido.tasker_partner.util.TaskerPartnerComponent;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.zip.Inflater;

import javax.inject.Inject;

public class BankAccountActivity extends SingleFragmentActivity {

    BankAccount mAccount;

    @Inject
    protected APIFetch mAPIFetch;

    @Override
    public void injectActivity(ApplicationComponent component) {
        ((TaskerPartnerComponent) component).inject(this);
    }

    protected Fragment createFragment() {
        return BankAccountFragment.newInstance(mAccount);
    }

    protected int getLayoutResId() {
        return R.layout.activity_fragment_toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        inflateOnCreate = false;
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(getString(R.string.bank_account_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mAPIFetch.get("users/account.json", null, new APIResponseHandler(this, getSupportFragmentManager(), true) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    mAccount = new BankAccount(response);
                    inflateFragment();
                } catch (Exception e) {
                    e.printStackTrace();
                    ErrorHandler.handleError(getSupportFragmentManager(), -1, "Error");
                }
                super.onSuccess(statusCode, headers, response);
            }
        });
    }

}
