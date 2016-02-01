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

import com.gruporaido.tasker_library.event.BackgroundLocationEvent;
import com.gruporaido.tasker_library.event.LocationEvent;
import com.gruporaido.tasker_library.fragment.BaseFragment;
import com.gruporaido.tasker_library.util.ApplicationComponent;
import com.gruporaido.tasker_library.util.Helper;
import com.gruporaido.tasker_partner.R;
import com.gruporaido.tasker_partner.util.Lab;
import com.gruporaido.tasker_partner.util.TaskerPartnerComponent;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class JobWaitingFragment extends BaseFragment {

    @Inject
    protected Lab mLab;

    @Override
    public void injectFragment(ApplicationComponent component) {
        ((TaskerPartnerComponent) component).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_waiting, parent, false);

        inflateFragment(R.id.job_waiting_availableLayout, new Helper.FragmentCreator() {
            @Override
            public Fragment createFragment() {
                return AvailableFragment.newInstance(mLab.getUser());
            }
        }, R.animator.no_animation, R.animator.no_animation);

        inflateFragment(R.id.job_waiting_toastLayout, new Helper.FragmentCreator() {
            @Override
            public Fragment createFragment() {
                return ToastFragment.newInstance(getString(R.string.toast_wait));
            }
        }, R.animator.no_animation, R.animator.no_animation);

        return view;
    }

    @Subscribe
    public void onBackgroundLocationEvent(BackgroundLocationEvent event) {
        mBus.post(new LocationEvent(event.getLatitude(), event.getLongitude()));
    }

}
