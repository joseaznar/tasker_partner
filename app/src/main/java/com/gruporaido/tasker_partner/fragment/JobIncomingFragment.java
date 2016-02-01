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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.gruporaido.tasker_library.communication.TargetListener;
import com.gruporaido.tasker_library.event.BackgroundDriveTimeEvent;
import com.gruporaido.tasker_library.event.FromToMarkersEvent;
import com.gruporaido.tasker_library.fragment.FragmentResponder;
import com.gruporaido.tasker_library.fragment.JobDetailsFragment;import com.gruporaido.tasker_library.http.APIResponseHandler;
import com.gruporaido.tasker_library.model.Job;
import com.gruporaido.tasker_library.util.ApplicationComponent;
import com.gruporaido.tasker_library.util.Helper;
import com.gruporaido.tasker_partner.R;
import com.gruporaido.tasker_partner.util.APIFetch;
import com.gruporaido.tasker_partner.util.Lab;
import com.gruporaido.tasker_partner.util.TaskerPartnerComponent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class JobIncomingFragment extends FragmentResponder {

    @Inject
    protected Lab mLab;

    @Inject
    protected Bus mBus;

    @Inject
    protected APIFetch mAPIFetch;

    protected Job mJob;

    protected ArrayList<FloatingActionButton> mFloatingActionButtons;


    @Bind(R.id.job_incoming_actionsFloatingActionsMenu)
    protected FloatingActionsMenu mActionsFloatingActionsMenu;

    @Override
    public void injectFragment(ApplicationComponent component) {
        ((TaskerPartnerComponent) component).inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mJob = mLab.getJob();


        mFloatingActionButtons = new ArrayList<>();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_incoming, parent, false);
        ButterKnife.bind(this, view);

        inflateFragment(R.id.job_incoming_jobLayout, new Helper.FragmentCreator() {
            @Override
            public Fragment createFragment() {
                return JobDetailsFragment.newInstance(mJob, JobDetailsFragment.Flags.JOB_BOTTOM | JobDetailsFragment.Flags.USER_TOP);
            }
        }, R.animator.slide_down_in, R.animator.slide_down_out);

        inflateActionsMenu();

        return view;
    }

    @Subscribe
    public void onBackgroundDriveTimeEvent(BackgroundDriveTimeEvent event) {
        FromToMarkersEvent fromToMarkersEvent = new FromToMarkersEvent(
                event.getLatitude(),
                event.getLongitude(),
                mJob.getLatitude(),
                mJob.getLongitude(),
                event.getDriveTime()
        );
        mBus.post(fromToMarkersEvent);
    }

    protected void inflateActionsMenu() {
        clearActionsMenu();
        switch (mJob.getState()) {
            case Job.State.Incoming:
                mActionsFloatingActionsMenu.setVisibility(View.VISIBLE);

                FloatingActionButton arrivedActionButton = createActionButton(getString(R.string.action_work), R.mipmap.bt_location_person);
                arrivedActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        arrived();
                    }
                });
                mActionsFloatingActionsMenu.addButton(arrivedActionButton);

                FloatingActionButton callActionButton = createActionButton(getString(R.string.action_call), R.mipmap.bt_phone);
                callActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel: " + mJob.getUser().getPhone()));
                        startActivity(callIntent);
                    }
                });
                mActionsFloatingActionsMenu.addButton(callActionButton);

                FloatingActionButton mapsActionButton = createActionButton(getString(R.string.action_maps), R.mipmap.bt_google_maps);
                mapsActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", mJob.getLatitude(), mJob.getLongitude());
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(intent);
                    }
                });
                mActionsFloatingActionsMenu.addButton(mapsActionButton);
                break;
        }
    }

    protected FloatingActionButton createActionButton(String title, int icon) {
        FloatingActionButton floatingActionButton = new FloatingActionButton(getActivity());
        floatingActionButton.setColorNormalResId(R.color.white);
        floatingActionButton.setColorPressedResId(R.color.white_pressed);
        floatingActionButton.setIcon(icon);
        floatingActionButton.setSize(FloatingActionButton.SIZE_MINI);
        floatingActionButton.setTitle(title);
        mFloatingActionButtons.add(floatingActionButton);
        return floatingActionButton;
    }

    protected void clearActionsMenu() {
        mActionsFloatingActionsMenu.setVisibility(View.GONE);
        Iterator<FloatingActionButton> iterator = mFloatingActionButtons.iterator();
        while (iterator.hasNext()) {
            mActionsFloatingActionsMenu.removeButton(iterator.next());
        }
    }

    protected void arrived() {
        mAPIFetch.patch("jobs/arrived.json", null, new APIResponseHandler(getActivity(), getActivity().getSupportFragmentManager(), true) {
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
