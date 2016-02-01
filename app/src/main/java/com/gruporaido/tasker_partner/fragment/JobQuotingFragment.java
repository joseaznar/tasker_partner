/**
 * GRUPO RAIDO CONFIDENTIAL
 * __________________
 *
 * [2015] - [2015] Grupo Raido Incorporated
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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gruporaido.tasker_library.communication.TargetListener;
import com.gruporaido.tasker_library.event.TextMarkerEvent;
import com.gruporaido.tasker_library.fragment.FragmentResponder;
import com.gruporaido.tasker_library.fragment.JobDetailsFragment;
import com.gruporaido.tasker_library.model.Job;
import com.gruporaido.tasker_library.util.ApplicationComponent;
import com.gruporaido.tasker_library.util.Helper;
import com.gruporaido.tasker_partner.R;
import com.gruporaido.tasker_partner.util.Lab;
import com.gruporaido.tasker_partner.util.TaskerPartnerComponent;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class JobQuotingFragment extends FragmentResponder {

    public static final int REQUEST_CREATE_QUOTE = 0;

    @Inject
    protected Lab mLab;

    @Inject
    protected Bus mBus;

    protected Job mJob;


    @Override
    public void injectFragment(ApplicationComponent component) {
        ((TaskerPartnerComponent) component).inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mJob = mLab.getJob();
        mBus.post(new TextMarkerEvent(getString(R.string.maps_there), mJob.getLatitude(), mJob.getLongitude(), 18));
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_quoting, parent, false);
        ButterKnife.bind(this, view);

        inflateFragment(R.id.job_quoting_jobLayout, new Helper.FragmentCreator() {
            @Override
            public Fragment createFragment() {
                return JobDetailsFragment.newInstance(mJob, JobDetailsFragment.Flags.USER_TOP | JobDetailsFragment.Flags.TASKER);
            }
        }, R.animator.slide_down_in, R.animator.slide_down_out);

        return view;
    }

    @OnClick(R.id.job_quoting_actionButton)
    public void onActionClick() {
        sendResult(TargetListener.RESULT_OK);
    }

    private void sendResult(int resultCode) {
        if (getTargetListener() == null) {
            return;
        }

        getTargetListener().onResult(getRequestCode(), resultCode, null);
    }


}
