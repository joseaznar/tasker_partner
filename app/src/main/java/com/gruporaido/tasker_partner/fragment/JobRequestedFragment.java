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
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gruporaido.tasker_library.util.Helper;
import com.gruporaido.tasker_partner.R;

import javax.inject.Inject;


public class JobRequestedFragment extends JobFlowFragment {

    @Inject
    protected Helper mHelper;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_requested, parent, false);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        mHelper.inflateFragment(fragmentManager, R.id.job_requested_topLayout, new Helper.FragmentCreator() {
            @Override
            public Fragment createFragment() {
                return null;//JobDetailsFragment.newInstance(mJob, JobDetailsFragment.Flags.JOB_BOTTOM);
            }
        }, R.animator.slide_up_in, R.animator.slide_down_out);

        mHelper.inflateFragment(fragmentManager, R.id.job_requested_bottomLayout, new Helper.FragmentCreator() {
            @Override
            public Fragment createFragment() {
                return ToastFragment.newInstance(getString(R.string.toast_requesting));
            }
        }, R.animator.slide_up_in, R.animator.slide_down_out);

        return view;
    }

}
