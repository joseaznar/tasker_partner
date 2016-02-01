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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.gruporaido.tasker_library.communication.TargetListener;
import com.gruporaido.tasker_library.fragment.BaseFragment;
import com.gruporaido.tasker_library.fragment.FragmentResponder;
import com.gruporaido.tasker_library.fragment.JobCardFragment;
import com.gruporaido.tasker_library.fragment.UserCardFragment;
import com.gruporaido.tasker_library.model.Request;
import com.gruporaido.tasker_library.util.ApplicationComponent;
import com.gruporaido.tasker_library.util.Helper;
import com.gruporaido.tasker_partner.R;
import com.gruporaido.tasker_partner.util.TaskerPartnerComponent;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RequestFragment extends FragmentResponder {

    public static final String EXTRA_REQUEST = "com.gruporaido.tasker.extra_request";

    protected Request mRequest;

    public static RequestFragment newInstance(Request request) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_REQUEST, request);

        RequestFragment fragment = new RequestFragment();
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
        mRequest = (Request) getArguments().getSerializable(EXTRA_REQUEST);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request, parent, false);
        ButterKnife.bind(this, view);

        inflateFragment(R.id.request_topLayout, new Helper.FragmentCreator() {
            @Override
            public Fragment createFragment() {
                return UserCardFragment.newInstance(mRequest.getJob().getUser(), UserCardFragment.Flags.RATINGS);
            }
        }, R.animator.slide_up_in, R.animator.slide_down_out);

        inflateFragment(R.id.request_bottomLayout, new Helper.FragmentCreator() {
            @Override
            public Fragment createFragment() {
                return JobCardFragment.newInstance(mRequest.getJob());
            }
        }, R.animator.slide_up_in, R.animator.slide_down_out);

        return view;
    }

    @OnClick(R.id.request_acceptButton)
    public void onAcceptClicked() {
        mRequest.setState(Request.States.Accepted);
        sendResult(TargetListener.RESULT_OK, mRequest);
    }

    @OnClick(R.id.request_rejectButton)
    public void onRejectClicked() {
        mRequest.setState(Request.States.Rejected);
        sendResult(TargetListener.RESULT_OK, mRequest);
    }

    protected void sendResult(int resultCode, Request request) {
        if (getTargetListener() == null) {
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_REQUEST, request);

        getTargetListener().onResult(getRequestCode(), resultCode, data);
    }
}
