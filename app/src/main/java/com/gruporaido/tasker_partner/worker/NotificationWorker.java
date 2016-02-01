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

package com.gruporaido.tasker_partner.worker;

import android.content.Context;
import android.content.Intent;

import com.gruporaido.tasker_library.event.JobEvent;
import com.gruporaido.tasker_library.event.RequestEvent;
import com.gruporaido.tasker_library.model.Job;
import com.gruporaido.tasker_library.model.Request;

import com.gruporaido.tasker_partner.activity.MainActivity;
import com.gruporaido.tasker_partner.service.AlarmService;
import com.gruporaido.tasker_partner.util.Lab;


public class NotificationWorker extends com.gruporaido.tasker_library.worker.NotificationWorker {

    protected Lab mLab;

    public NotificationWorker(Context context, Intent intent, boolean isInForeground, Lab lab) {
        super(context, intent, isInForeground);
        mLab = lab;
    }

    @Override
    public void call(String type) {
        switch (type) {
            case Type.Request:
                launchAlarm();
                try {
                    Request request = getRequest();
                    saveRequest(request);
                    publishRequestUpdate(request);
                    launchActivityWithRequest(MainActivity.class, MainActivity.EXTRA_REQUEST, request);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case Type.Incoming:
            case Type.Quoting:
            case Type.QuoteAccepted:
            case Type.Working:
            case Type.Rating:
            case Type.Finished:
            case Type.Cancelled:
            case Type.Rejected:
            case Type.Failed:
                try {
                    Job job = getJob();
                    saveJob(job);
                    publishJobUpdate(job);
                    launchActivityWithJob(MainActivity.class, MainActivity.EXTRA_JOB, job);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case Type.Location:
                try {
                    Job job = mLab.getJob();
                    job.setLastLocation(getLocation());
                    saveJob(job);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case Type.Request_Cancelled:
                stopAlarm();
                deleteRequest();
                publishRequestUpdate(null);
                break;
        }
    }

    protected void launchAlarm() {
        if (!mIsInForeground) {
            Intent alarmIntent = new Intent(mContext, AlarmService.class);
            mContext.startService(alarmIntent);
        }
    }

    protected void stopAlarm() {
        if (!mIsInForeground) {
            Intent destroyIntent = new Intent(mContext, AlarmService.class);
            mContext.stopService(destroyIntent);
        }
    }

    protected void publishJobUpdate(Job job) {
        mBus.post(new JobEvent(job));
    }

    protected void publishRequestUpdate(Request request) {
        mBus.post(new RequestEvent(request));
    }

    protected void saveJob(Job job) {
        mLab.setJob(job).saveJob();
    }

    protected void saveRequest(Request request) {
        mLab.setRequest(request).saveRequest();
    }

    protected void deleteRequest() {
        mLab.deleteRequest();
    }
}
