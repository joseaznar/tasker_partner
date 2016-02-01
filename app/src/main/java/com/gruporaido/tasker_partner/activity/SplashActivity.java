/**
 * GRUPO RAIDO CONFIDENTIAL
 * __________________
 * <p/>
 * [2015] - [2015] Grupo Raido Incorporated
 * All Rights Reserved.
 * <p/>
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


import android.content.Intent;
import android.os.Bundle;

import com.gruporaido.tasker_library.activity.DaggerActivity;
import com.gruporaido.tasker_library.http.APIResponseHandler;
import com.gruporaido.tasker_library.model.Job;
import com.gruporaido.tasker_library.report.Logging;
import com.gruporaido.tasker_library.report.SentryReporter;
import com.gruporaido.tasker_library.util.ApplicationComponent;
import com.gruporaido.tasker_partner.R;
import com.gruporaido.tasker_partner.util.APIFetch;
import com.gruporaido.tasker_partner.util.Lab;
import com.gruporaido.tasker_partner.util.TaskerPartnerComponent;

import org.apache.http.Header;
import org.json.JSONObject;

import javax.inject.Inject;

public class SplashActivity extends DaggerActivity {

    private static final String TAG = "SplashActivity";

    @Inject
    protected APIFetch mAPIFetch;

    @Inject
    protected Lab mLab;

    @Override
    public void injectActivity(ApplicationComponent component) {
        ((TaskerPartnerComponent) component).inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        checkForUser();

    }

    protected void checkForUser() {
        if (mLab.getUser().isEmpty()) {
            launchLogin();
        } else {
            loadActiveJob();
        }
    }

    /**
     * Save the current active job if there's one. Delete any if not.
     */
    protected void loadActiveJob() {
        mAPIFetch.get("jobs/active.json", null, new APIResponseHandler(this, getSupportFragmentManager(), false) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Logging.getLogger(SentryReporter.class).debug(TAG, response.toString());
                try {
                    mLab.setJob(new Job(response)).saveJob();
                    launchMain();
                } catch (Exception e) {
                    e.printStackTrace();
                    mLab.deleteJob();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                switch (statusCode) {
                    case 404:
                        mLab.deleteJob();
                        launchMain();
                        break;
                    default:
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            }

            @Override
            public void onRetry() {
                super.onRetry();
                loadActiveJob();
            }
        });
    }

    protected void launchLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    protected void launchMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}