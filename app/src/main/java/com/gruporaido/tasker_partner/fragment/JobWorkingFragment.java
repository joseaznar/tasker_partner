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
import com.gruporaido.tasker_library.fragment.FragmentResponder;
import com.gruporaido.tasker_library.fragment.JobDetailsFragment;
import com.gruporaido.tasker_library.http.APIResponseHandler;
import com.gruporaido.tasker_library.model.Job;
import com.gruporaido.tasker_library.util.ApplicationComponent;
import com.gruporaido.tasker_library.util.Helper;
import com.gruporaido.tasker_partner.R;
import com.gruporaido.tasker_partner.util.APIFetch;
import com.gruporaido.tasker_partner.util.Lab;
import com.gruporaido.tasker_partner.util.TaskerPartnerComponent;

import org.apache.http.Header;
import org.json.JSONObject;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class JobWorkingFragment extends FragmentResponder {

    @Inject
    protected Lab mLab;

    @Inject
    protected APIFetch mAPIFetch;

    @Inject
    protected Helper mHelper;

    protected Job mJob;

    @Override
    public void injectFragment(ApplicationComponent component) {
        ((TaskerPartnerComponent) component).inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mJob = mLab.getJob();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_working, parent, false);
        ButterKnife.bind(this, view);

        mHelper.inflateFragment(getActivity().getSupportFragmentManager(), R.id.job_working_jobLayout, new Helper.FragmentCreator() {
            @Override
            public Fragment createFragment() {
                return JobDetailsFragment.newInstance(mJob, JobDetailsFragment.Flags.USER_TOP | JobDetailsFragment.Flags.TASKER);
            }
        }, R.animator.slide_down_in, R.animator.slide_down_out);

        return view;
    }

    @OnClick(R.id.job_working_actionButton)
    public void onActionClick() {
        worked();
    }

    protected void worked() {
        mAPIFetch.patch("jobs/worked.json", null, new APIResponseHandler(getActivity(), getActivity().getSupportFragmentManager(), true) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    mJob = new Job(response);
                    mLab.setJob(mJob).saveJob();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sendResult(TargetListener.RESULT_OK);
                super.onSuccess(statusCode, headers, response);
            }
        });
    }

    private void sendResult(int resultCode) {
        if (getTargetListener() == null) {
            return;
        }

        getTargetListener().onResult(getRequestCode(), resultCode, null);
    }
}
