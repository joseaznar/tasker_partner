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

package com.gruporaido.tasker_partner.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.dd.CircularProgressButton;
import com.gruporaido.tasker_library.communication.TargetListener;
import com.gruporaido.tasker_library.fragment.FragmentResponder;
import com.gruporaido.tasker_library.http.APIResponseHandler;
import com.gruporaido.tasker_library.http.ErrorHandler;
import com.gruporaido.tasker_library.model.Session;
import com.gruporaido.tasker_library.model.User;
import com.gruporaido.tasker_library.report.Logging;
import com.gruporaido.tasker_library.report.SentryReporter;
import com.gruporaido.tasker_library.util.ApplicationComponent;
import com.gruporaido.tasker_library.util.GcmRegistration;
import com.gruporaido.tasker_partner.R;
import com.gruporaido.tasker_partner.util.APIFetch;
import com.gruporaido.tasker_partner.util.Lab;
import com.gruporaido.tasker_partner.util.TaskerPartnerComponent;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.Exception;
import java.lang.Override;
import java.lang.String;

import javax.inject.Inject;

public class LoginFragment extends FragmentResponder {

    private static final String TAG = "LoginFragment";

    public static final String EXTRA_USER = "com.gruporaido.tasker.extra_user";

    public static final int REQUEST_REGISTER = 0;

    @Inject
    protected APIFetch mAPIFetch;

    @Inject
    protected Lab mLab;

    protected CircularProgressButton mLoginButton;
    protected EditText mEmailEditText;
    protected EditText mPasswordEditText;

    @Override
    public void injectFragment(ApplicationComponent component) {
        ((TaskerPartnerComponent) component).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, parent, false);

        mEmailEditText = (EditText) view.findViewById(R.id.splash_emailEditText);

        mPasswordEditText = (EditText) view.findViewById(R.id.splash_passwordEditText);

        mLoginButton = (CircularProgressButton) view.findViewById(R.id.splash_loginButton);
        mLoginButton.setIndeterminateProgressMode(true);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams params = new RequestParams();
                params.put(User.JSON_EMAIL, mEmailEditText.getText());
                params.put(User.JSON_PASSWORD, mPasswordEditText.getText());
                try {
                    doLogin("plain", params);
                } catch (Exception e) {
                    Logging.getLogger(SentryReporter.class).error(TAG, "Error doing login: ", e);
                } finally {

                }
            }
        });

        return view;
    }

    protected void lockView() {
        mLoginButton.setProgress(0);
        mLoginButton.setProgress(1);
        switchControlState(false);
    }

    protected void unlockView() {
        switchControlState(true);
    }

    protected void switchControlState(boolean state) {
        mEmailEditText.setEnabled(state);
        mPasswordEditText.setEnabled(state);
    }

    private void doLogin(final String type, final RequestParams params) throws JSONException {
        lockView();
        GcmRegistration.get(getActivity()).register(new GcmRegistration.Callbacks() {
            @Override
            public void onRegister(Session session) {
                params.put(Session.JSON_PLATFORM, session.getPlatform());
                params.put(Session.JSON_PUSH_ID, session.getPushId());
                mAPIFetch.post("sessions/" + type + ".json", params, new APIResponseHandler(getActivity(), getActivity().getSupportFragmentManager(), false) {
                    @Override
                    public void onRetry() {
                        try {
                            doLogin(type, params);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            loginSuccess(new User(response));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFinish(boolean success) {
                        int progress = 0;
                        if (success) progress = -1;
                        mLoginButton.setProgress(progress);
                        unlockView();
                    }
                });
            }
        }, getActivity());
    }

    protected void loginSuccess(User user) {
        if (mLab.setUser(user).saveUser()) {
            sendResult(TargetListener.RESULT_OK, user);
        } else {
            ErrorHandler.handleError(getActivity().getSupportFragmentManager(), -1, "Error");
        }
    }

    protected void sendResult(int resultCode, User user) {
        if (getTargetListener() == null) return;

        Intent intent = new Intent();
        intent.putExtra(EXTRA_USER, (Parcelable) user);

        getTargetListener().onResult(getRequestCode(), resultCode, intent);
    }
}
