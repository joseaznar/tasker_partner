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

import com.gruporaido.tasker_library.fragment.FragmentResponder;
import com.gruporaido.tasker_library.model.Job;

public class JobFlowFragment extends FragmentResponder {

    public static final String EXTRA_JOB = "com.gruporaido.taker.extra_job";

    protected Job mJob;

    public static JobFlowFragment newInstance(Job job) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(EXTRA_JOB, job);

        JobFlowFragment fragment = new JobFlowFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mJob = (Job) getArguments().getSerializable(EXTRA_JOB);
    }
}
