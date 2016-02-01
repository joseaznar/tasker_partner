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

import android.app.Activity;
import android.content.Intent;
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
import com.gruporaido.tasker_partner.util.TaskerPartnerComponent;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class JobQuotingFormFragment extends FragmentResponder implements TargetListener {

    public static final String EXTRA_JOB = "com.gruporaido.tasker_library.extra_job";

    private static final int REQUEST_QUOTE_FORM = 0;

    @Inject
    protected Bus mBus;

    @Inject
    protected Helper mHelper;

    protected Job mJob;

    public static JobQuotingFormFragment newInstance(Job job) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_JOB, job);

        JobQuotingFormFragment fragment = new JobQuotingFormFragment();
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
        mJob = (Job) getArguments().getSerializable(EXTRA_JOB);
        mBus.post(new TextMarkerEvent(getString(R.string.maps_there), mJob.getLatitude(), mJob.getLongitude(), 18));
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_quoting_form, parent, false);
        ButterKnife.bind(this, view);

        inflateFragment(R.id.quoting_form_userLayout, new Helper.FragmentCreator() {
            @Override
            public Fragment createFragment() {
                return JobDetailsFragment.newInstance(mJob, JobDetailsFragment.Flags.USER_TOP | JobDetailsFragment.Flags.TASKER);
            }
        }, R.animator.slide_up_in, R.animator.slide_down_out);

        inflateFragment(R.id.quoting_form_quoteLayout, new Helper.FragmentCreator() {
            @Override
            public Fragment createFragment() {
                QuoteFormFragment fragment = QuoteFormFragment.newInstance(mJob);
                fragment.setTargetFragment(JobQuotingFormFragment.this, REQUEST_QUOTE_FORM);
                return fragment;
            }
        }, R.animator.slide_up_in, R.animator.slide_down_out);

        return view;
    }

    @Override
    public void onResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != TargetListener.RESULT_OK) return;

        switch (requestCode) {
            case REQUEST_QUOTE_FORM:
                mJob = (Job) data.getSerializableExtra(QuoteFormFragment.EXTRA_JOB);
                sendResult(TargetListener.RESULT_OK, mJob);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        switch (requestCode) {
            case REQUEST_QUOTE_FORM:
                mJob = (Job) data.getSerializableExtra(QuoteFormFragment.EXTRA_JOB);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void sendResult(int resultCode, Job job) {
        if (getTargetListener() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_JOB, job);

        getTargetListener().onResult(getRequestCode(), resultCode, null);
    }


}
