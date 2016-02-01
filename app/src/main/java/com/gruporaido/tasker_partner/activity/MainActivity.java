package com.gruporaido.tasker_partner.activity;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.gruporaido.tasker_library.activity.AboutActivity;
import com.gruporaido.tasker_library.activity.DaggerActivity;
import com.gruporaido.tasker_library.activity.SupportActivity;
import com.gruporaido.tasker_library.communication.TargetListener;
import com.gruporaido.tasker_library.event.JobEvent;
import com.gruporaido.tasker_library.event.RequestEvent;
import com.gruporaido.tasker_library.fragment.MapsFragment;
import com.gruporaido.tasker_library.http.APIResponseHandler;
import com.gruporaido.tasker_library.model.Job;
import com.gruporaido.tasker_library.model.Request;
import com.gruporaido.tasker_library.model.User;
import com.gruporaido.tasker_library.receiver.NotificationBroadcastReceiver;
import com.gruporaido.tasker_library.util.ApplicationComponent;
import com.gruporaido.tasker_library.util.Helper;
import com.gruporaido.tasker_partner.R;
import com.gruporaido.tasker_partner.fragment.JobIncomingFragment;
import com.gruporaido.tasker_partner.fragment.JobQuoteAcceptanceFragment;
import com.gruporaido.tasker_partner.fragment.JobQuotingFormFragment;
import com.gruporaido.tasker_partner.fragment.JobQuotingFragment;
import com.gruporaido.tasker_partner.fragment.JobWaitingFragment;
import com.gruporaido.tasker_partner.fragment.JobWorkingFragment;
import com.gruporaido.tasker_partner.fragment.RequestFragment;
import com.gruporaido.tasker_partner.service.AlarmService;
import com.gruporaido.tasker_partner.service.LocationManagerService;
import com.gruporaido.tasker_partner.util.APIFetch;
import com.gruporaido.tasker_partner.util.Lab;
import com.gruporaido.tasker_partner.util.TaskerPartnerComponent;
import com.gruporaido.tasker_partner.worker.NotificationWorker;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.apache.http.Header;
import org.json.JSONObject;

import javax.inject.Inject;

public class MainActivity
        extends DaggerActivity
        implements NavigationView.OnNavigationItemSelectedListener, TargetListener {

    private static final String TAG = "MainActivity";

    public static final String EXTRA_REQUEST = "com.gruporaido.tasker.extra_request";
    public static final String EXTRA_JOB = "com.gruporaido.tasker.extra_job";

    protected static final int REQUEST_LOCATION_ACCESS = 0;
    protected static final int REQUEST_RESPONSE = 1;
    protected static final int REQUEST_INCOMING = 2;
    protected static final int REQUEST_QUOTING = 3;
    protected static final int REQUEST_QUOTING_FORM = 4;
    protected static final int REQUEST_QUOTE_ACCEPTANCE = 5;
    protected static final int REQUEST_WORKING = 6;

    @Inject
    protected APIFetch mAPIFetch;

    @Inject
    protected Lab mLab;

    @Inject
    protected Helper mHelper;

    @Inject
    protected Bus mBus;

    protected User mUser;
    protected Job mJob;
    protected Request mRequest;

    protected GcmReceiver mGcmReceiver;

    @Override
    public void injectActivity(ApplicationComponent component) {
        ((TaskerPartnerComponent) component).inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        askLocationPermissions();

        mJob = mLab.getJob();
        inflateInterface(mJob);
        inflateMap();
        mBus.register(this);
        startLocationService();
    }

    /**
     * Register:
     * <ul>
     * <li>{@link GcmReceiver} for GCM notifications</li>
     * </ul>
     *
     * Close all notifications in the Notification Service.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (mGcmReceiver == null) {
            mGcmReceiver = new GcmReceiver();
        }
        IntentFilter intentFilter = new IntentFilter(NotificationBroadcastReceiver.REFRESH_DATA_INTENT);
        intentFilter.setPriority(1);
        registerReceiver(mGcmReceiver, intentFilter);
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
    }

    /**
     * Unregister:
     * <ul>
     * <li>{@link GcmReceiver}</li>
     * </ul>
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGcmReceiver != null) {
            unregisterReceiver(mGcmReceiver);
            mGcmReceiver = null;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_balance:
                //launchActivity(BalanceActivity.class);
                break;
            case R.id.nav_account:
                //launchActivity(BankAccountActivity.class);
                break;
            case R.id.nav_services:
                //launchActivity(ServiceHistoryActivity.class);
                break;
            case R.id.nav_support:
                launchActivity(SupportActivity.class);
                break;
            case R.id.nav_about:
                launchActivity(AboutActivity.class);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_ACCESS: {
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    askLocationPermissions();
                }
                return;
            }
        }
    }

    protected boolean askLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_ACCESS);
            return true;
        }
        return false;
    }

    protected void startLocationService() {
        Intent intent = new Intent(this, LocationManagerService.class);
        startService(intent);
    }

    protected void stopBackgroundServices() {
        Intent destroyIntent = new Intent(this, AlarmService.class);
        stopService(destroyIntent);
    }

    protected void launchActivity(Class klass) {
        Intent intent = new Intent(this, klass);
        startActivity(intent);
    }

    /**
     * {@link }
     * Inflate a new fragment to the main container.
     *
     * @param fragment
     */
    public void onInflateFragment(final Fragment fragment, boolean addToBackStack) {
        inflateFragment(R.id.content_frame, new Helper.FragmentCreator() {
            @Override
            public Fragment createFragment() {
                return fragment;
            }
        }, addToBackStack);
    }

    /**
     * Inflate fragments without animation
     *
     * @param resourceId
     * @param creator
     */
    private void inflateFragment(int resourceId, Helper.FragmentCreator creator) {
        mHelper.inflateFragment(getSupportFragmentManager(), resourceId, creator, R.animator.no_animation, R.animator.no_animation, false);
    }

    /**
     * Inflate fragments without animation
     *
     * @param resourceId
     * @param creator
     */
    private void inflateFragment(int resourceId, Helper.FragmentCreator creator, boolean addtoBackstack) {
        mHelper.inflateFragment(getSupportFragmentManager(), resourceId, creator, R.animator.no_animation, R.animator.no_animation, addtoBackstack);
    }

    @Override
    public void onResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode != TargetListener.RESULT_OK) return;

        switch (requestCode) {
            case REQUEST_RESPONSE:
                mRequest = (Request) data.getSerializableExtra(RequestFragment.EXTRA_REQUEST);
                changeRequestState(mRequest);
                break;
            case REQUEST_INCOMING:
                inflateInterface(mJob);
                break;
            case REQUEST_QUOTING:
                inflateFragment(R.id.content_frame, new Helper.FragmentCreator() {
                    @Override
                    public Fragment createFragment() {
                        JobQuotingFormFragment fragment = JobQuotingFormFragment.newInstance(mJob);
                        return fragment;
                    }
                });
                break;
            case REQUEST_WORKING:
                inflateInterface(mJob);
                break;

        }
    }

    @Subscribe
    public void onJobEvent(JobEvent event) {
        mJob = event.getJob();
        inflateInterface(mJob);
    }

    @Subscribe
    public void onRequestEvent(RequestEvent event) {
        mRequest = event.getRequest();
        if (mRequest != null) {
            inflateFragment(R.id.content_frame, new Helper.FragmentCreator() {
                @Override
                public Fragment createFragment() {
                    RequestFragment fragment = RequestFragment.newInstance(mRequest);
                    fragment.setTargetListener(MainActivity.this, REQUEST_RESPONSE);
                    return fragment;
                }
            });
        } else {
            inflateInterface(mJob);
        }
    }

    protected void inflateMap() {
        inflateFragment(R.id.mapsFrame, new Helper.FragmentCreator() {
            @Override
            public Fragment createFragment() {
                return new MapsFragment();
            }
        });
    }

    protected void inflateInterface(Job job) {
        if (job.isEmpty()) {
            inflateFragment(R.id.content_frame, new Helper.FragmentCreator() {
                @Override
                public Fragment createFragment() {
                    return new JobWaitingFragment();
                }
            });
        } else {
            switch (job.getState()) {
                case Job.State.Incoming:
                    inflateFragment(R.id.content_frame, new Helper.FragmentCreator() {
                        @Override
                        public Fragment createFragment() {
                            JobIncomingFragment fragment = new JobIncomingFragment();
                            fragment.setTargetListener(MainActivity.this, REQUEST_INCOMING);
                            return fragment;
                        }
                    });
                    break;
                case Job.State.Quoting:
                    inflateFragment(R.id.content_frame, new Helper.FragmentCreator() {
                        @Override
                        public Fragment createFragment() {
                            JobQuotingFragment fragment = new JobQuotingFragment();
                            fragment.setTargetListener(MainActivity.this, REQUEST_QUOTING);
                            return fragment;
                        }
                    });
                    break;
                case Job.State.QuoteAcceptance:
                    inflateFragment(R.id.content_frame, new Helper.FragmentCreator() {
                        @Override
                        public Fragment createFragment() {
                            JobQuoteAcceptanceFragment fragment = new JobQuoteAcceptanceFragment();
                            fragment.setTargetListener(MainActivity.this, REQUEST_QUOTE_ACCEPTANCE);
                            return fragment;
                        }
                    });
                    break;
                case Job.State.Working:
                    inflateFragment(R.id.content_frame, new Helper.FragmentCreator() {
                        @Override
                        public Fragment createFragment() {
                            JobWorkingFragment fragment = new JobWorkingFragment();
                            fragment.setTargetListener(MainActivity.this, REQUEST_WORKING);
                            return fragment;
                        }
                    });
                    break;
                case Job.State.Rejected:
                case Job.State.UserCancelled:
                case Job.State.Rating:
                    break;
            }
        }
        //mGoogleMap.getUiSettings().setAllGesturesEnabled(false);
    }

    /**
     * Accept or reject a job request. Only a Provider can do this.
     * <b><i>Note: The request might have expired or is soon to expire.</i></b>
     *
     * @param request
     */
    private void changeRequestState(Request request) {
        String action = "accept.json";
        if (request.getState().equals(Request.States.Rejected)) {
            action = "reject.json";
        }

        mAPIFetch.post("requests/" + action, request.buildParams(), new APIResponseHandler(this, getSupportFragmentManager(), true) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    mJob = new Job(response);
                    if (mJob.isEmpty()) {
                        mLab.deleteJob();
                    } else {
                        mLab.saveJob();
                    }
                    inflateInterface(mJob);
                } catch (Exception e) {
                    e.printStackTrace();
                    mLab.deleteJob();
                } finally {
                    mLab.deleteRequest();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                mLab.deleteRequest();
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    protected void processNotification(Intent intent) {
        NotificationWorker worker = new NotificationWorker(getApplicationContext(), intent, true, mLab);
        worker.call();
    }

    /**
     * Receiver for GCM broadcast. It is only active during the activity's life.
     * If not active it will fallback to {@link com.gruporaido.tasker_library.receiver.GcmBroadcastReceiver}
     */
    protected class GcmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(NotificationBroadcastReceiver.REFRESH_DATA_INTENT)) {
                processNotification(intent);
                abortBroadcast();
            }
        }
    }
}
