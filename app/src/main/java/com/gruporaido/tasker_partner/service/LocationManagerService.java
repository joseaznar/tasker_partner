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

package com.gruporaido.tasker_partner.service;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.gruporaido.tasker_library.event.BackgroundDriveTimeEvent;
import com.gruporaido.tasker_library.event.BackgroundLocationEvent;
import com.gruporaido.tasker_library.http.APIResponseHandler;
import com.gruporaido.tasker_library.model.Job;
import com.gruporaido.tasker_library.service.DaggerIntentService;
import com.gruporaido.tasker_library.util.ApplicationComponent;
import com.gruporaido.tasker_partner.util.APIFetch;
import com.gruporaido.tasker_partner.util.Lab;
import com.gruporaido.tasker_partner.util.TaskerPartnerComponent;
import com.squareup.otto.Bus;

import org.apache.http.Header;
import org.json.JSONObject;

import javax.inject.Inject;

public class LocationManagerService
        extends DaggerIntentService
        implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    private static final String TAG = "LocationManagerService";

    private static final int LOCATION_REQUEST_INTERVAL_ACTIVE = 30000;
    private static final int LOCATION_REQUEST_MAX_INTERVAL_ACTIVE = 15000;

    private static final int LOCATION_REQUEST_INTERVAL_INACTIVE = 60000;
    private static final int LOCATION_REQUEST_MAX_INTERVAL_INACTIVE = 30000;

    @Inject
    protected Bus mBus;

    @Inject
    protected Lab mLab;

    @Inject
    protected APIFetch mAPIFetch;

    protected GoogleApiClient mGoogleApiClient;
    protected String mLastState = null;

    public LocationManagerService() {
        super("LocationManagerService");
    }

    @Override
    public void injectService(ApplicationComponent component) {
        ((TaskerPartnerComponent) component).inject(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mLab.getUser().isTasker()) {
            buildGoogleApiClient();
            mGoogleApiClient.connect();
        } else {
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connection started");
        //mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mLab.getUser().isEmpty() || !mLab.getUser().isAvailable()) {
            stopSelf();
        } else if (mLab.getJob() != null && mLab.getJob().getState() != null && !mLab.getJob().getState().equals(mLastState)) {
            mLastState = mLab.getJob().getState();
            stopLocationUpdates();
            startLocationUpdates();
        }
        mBus.post(new BackgroundLocationEvent(location.getLatitude(), location.getLongitude()));
        saveLocation(new com.gruporaido.tasker_library.model.Location(location));
    }

    protected void saveLocation(final com.gruporaido.tasker_library.model.Location location) {
        mAPIFetch.post("locations.json", location.buildParams(), new APIResponseHandler(this, null, false) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Job job = mLab.getJob();
                    if (!job.isNew() && job.getState().equals(Job.State.Incoming)) {
                        com.gruporaido.tasker_library.model.Location newLocation = new com.gruporaido.tasker_library.model.Location(response);
                        job.setLastLocation(newLocation);
                        mLab.setJob(job).saveJob();
                        /*Intent intent = new Intent(NotificationBroadcastReceiver.REFRESH_DATA_INTENT);
                        Bundle extras = new Bundle();
                        //TODO change to constants
                        extras.putString("type", NotificationBroadcastReceiver.Type.Location);
                        extras.putString(com.gruporaido.tasker.model.Location.JSON_WRAPPER, newLocation.toJSON().toString());
                        intent.putExtras(extras);
                        sendOrderedBroadcast(intent, null);*/
                        BackgroundDriveTimeEvent event = new BackgroundDriveTimeEvent(
                                newLocation.getLatitude(),
                                newLocation.getLongitude(),
                                newLocation.getDriveTime()
                        );
                        mBus.post(event);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     *
     */
    protected void startLocationUpdates() {
        if (mGoogleApiClient != null) {
            if (Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, createLocationRequest(), this);
        }
    }

    /**
     *
     */
    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    /**
     *
     */
    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        int interval = LOCATION_REQUEST_INTERVAL_ACTIVE;
        int maxInterval = LOCATION_REQUEST_MAX_INTERVAL_ACTIVE;
        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY;
        if (mLab.getJob().isNew() || mLab.getJob().getState() != Job.State.Incoming) {
            interval = LOCATION_REQUEST_INTERVAL_INACTIVE;
            maxInterval = LOCATION_REQUEST_MAX_INTERVAL_INACTIVE;
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
        }
        locationRequest.setInterval(interval);
        locationRequest.setFastestInterval(maxInterval);
        locationRequest.setPriority(priority);
        return locationRequest;
    }

    /**
     *
     */
    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
}
