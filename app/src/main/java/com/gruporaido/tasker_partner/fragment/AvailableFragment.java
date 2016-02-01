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

package com.gruporaido.tasker_partner.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.gruporaido.tasker_library.fragment.BaseFragment;
import com.gruporaido.tasker_library.http.APIResponseHandler;
import com.gruporaido.tasker_library.model.User;
import com.gruporaido.tasker_library.report.Logging;
import com.gruporaido.tasker_library.report.SentryReporter;
import com.gruporaido.tasker_library.util.ApplicationComponent;
import com.gruporaido.tasker_partner.R;
import com.gruporaido.tasker_partner.util.APIFetch;
import com.gruporaido.tasker_partner.util.Lab;
import com.gruporaido.tasker_partner.util.TaskerPartnerComponent;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AvailableFragment extends BaseFragment {

    private static final String TAG = "PlacesFragment";

    public static final String EXTRA_USER = "com.gruporaido.tasker_partner.extra_user";

    @Inject
    protected APIFetch mAPIFetch;

    @Inject
    protected Lab mLab;

    protected User mUser;

    @Bind(R.id.available_toggleLayout)
    protected FrameLayout mToggleLayout;

    @Bind(R.id.available_toggleBackgroundButton)
    protected View mToggleBackgroundButton;

    @Bind(R.id.available_toggleForegroundButton)
    protected View mToggleForegroundButton;

    @Bind(R.id.available_availableTextView)
    protected TextView mAvailableTextView;

    public static AvailableFragment newInstance(User user) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_USER, user);

        AvailableFragment fragment = new AvailableFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void injectFragment(ApplicationComponent component) {
        ((TaskerPartnerComponent) component).inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = (User) getArguments().getSerializable(EXTRA_USER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_available, parent, false);
        ButterKnife.bind(this, view);

        mToggleBackgroundButton.setEnabled(false);
        mToggleForegroundButton.setEnabled(false);

        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                setState(mUser.isAvailable());
                mToggleBackgroundButton.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        return view;
    }

    @OnClick(R.id.available_toggleLayout)
    public void onToggleClicked() {
        mUser.setAvailable(!mUser.isAvailable());
        setState(mUser.isAvailable());
        makeRequest();
    }

    protected void updateAvailabity(boolean available) {
        if (available) {
            mAvailableTextView.setText(getString(R.string.action_available));
        } else {
            mAvailableTextView.setText(getString(R.string.action_not_available));
        }
    }

    protected void setState(boolean state) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mToggleForegroundButton.getWidth(), mToggleForegroundButton.getHeight());
        mToggleBackgroundButton.setEnabled(state);
        mToggleForegroundButton.setEnabled(state);
        params.gravity = (state ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL;
        mToggleForegroundButton.setLayoutParams(params);
    }

    protected void makeRequest() {
        RequestParams params = new RequestParams();
        params.put(User.JSON_AVAILABLE, mUser.isAvailable());

        mAPIFetch.patch("users/available.json", params, new APIResponseHandler(getActivity(), getActivity().getSupportFragmentManager(), true) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Logging.getLogger(SentryReporter.class).info(TAG, response.toString());
                try {
                    mUser = new User(response);
                    mLab.setUser(mUser).saveUser();
                    updateAvailabity(mUser.isAvailable());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }
        });
    }

}
