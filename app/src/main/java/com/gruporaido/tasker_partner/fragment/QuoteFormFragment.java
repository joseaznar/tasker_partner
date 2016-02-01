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

package com.gruporaido.tasker_partner.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gruporaido.tasker_library.fragment.FragmentResponder;
import com.gruporaido.tasker_library.http.APIResponseHandler;
import com.gruporaido.tasker_library.http.ErrorHandler;
import com.gruporaido.tasker_library.model.Errorable;
import com.gruporaido.tasker_library.model.Job;
import com.gruporaido.tasker_library.model.Quote;
import com.gruporaido.tasker_library.util.ApplicationComponent;
import com.gruporaido.tasker_library.util.Helper;
import com.gruporaido.tasker_partner.R;
import com.gruporaido.tasker_partner.util.APIFetch;
import com.gruporaido.tasker_partner.util.Lab;
import com.gruporaido.tasker_partner.util.TaskerPartnerComponent;
import com.rey.material.widget.EditText;

import org.apache.http.Header;
import org.json.JSONObject;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuoteFormFragment extends FragmentResponder implements Errorable {

    private static final String TAG = "QuoteFormFragment";

    public static final String EXTRA_JOB = "com.gruporaido.tasker.extra_job";

    protected static final int TIMER_LENGTH = 2000;

    @Inject
    protected Helper mHelper;

    @Inject
    protected APIFetch mAPIFetch;

    @Inject
    protected Lab mLab;

    protected Job mJob;
    protected Quote mQuote;

    @Bind(R.id.quote_form_laborEditText)
    protected EditText mLaborEditText;

    @Bind(R.id.quote_form_materialsEditText)
    protected EditText mMaterialsEditText;

    @Bind(R.id.quote_form_totalTextView)
    protected TextView mTotalTextView;

    @Bind(R.id.quote_form_loadingProgressBar)
    protected ProgressBar mLoadingProgressBar;

    protected CountDownTimer mCountDownTimer;

    public static QuoteFormFragment newInstance(Job job) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_JOB, job);

        QuoteFormFragment fragment = new QuoteFormFragment();
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
        mQuote = new Quote();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quote_form, parent, false);
        ButterKnife.bind(this, view);

        addTextListeners();

        return view;
    }

    @OnClick(R.id.quote_form_cancelButton)
    public void onCancelClick() {
        mQuote.setMaterialsCost(-1);
        mQuote.setLaborCost(-1);
        sendResult(Activity.RESULT_OK, mJob);
    }

    @OnClick(R.id.quote_form_acceptButton)
    public void onAcceptClick() {
        sendQuote(mQuote);
    }

    @Override
    public void setErrors(JSONObject errors) {
        clearErrors();
        try {
            ErrorHandler.addErrorsToEditText(mLaborEditText, errors, Quote.JSON_LABOR_COST);
            ErrorHandler.addErrorsToEditText(mMaterialsEditText, errors, Quote.JSON_MATERIALS_COST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void addTextListeners() {
        mLaborEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    mQuote.setLaborCost(Float.valueOf(s.toString()));
                    startTimer(TIMER_LENGTH);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mMaterialsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    mQuote.setMaterialsCost(Float.valueOf(s.toString()));
                    startTimer(TIMER_LENGTH);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    protected void clearErrors() {
        mLaborEditText.setError(null);
        mMaterialsEditText.setError(null);
    }

    protected void updateTotal() {
        mTotalTextView.setText(mHelper.formatDouble(mQuote.getTotal()));
    }

    protected void startTimer(final int time) {
        stopTimer();
        mCountDownTimer = new CountDownTimer(time, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                calculateQuote(mQuote);
            }
        };
        mCountDownTimer.start();
    }

    protected void stopTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    protected void calculateQuote(Quote quote) {
        mLoadingProgressBar.setVisibility(View.VISIBLE);
        mTotalTextView.setVisibility(View.GONE);
        mAPIFetch.post("jobs/" + mJob.getId() + "/quotes/calculate.json", quote.buildParams(), new APIResponseHandler(getActivity(), getActivity().getSupportFragmentManager(), false) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    mQuote = new Quote(response);
                    updateTotal();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mLoadingProgressBar.setVisibility(View.GONE);
                mTotalTextView.setVisibility(View.VISIBLE);
                super.onSuccess(statusCode, headers, response);
            }
        });
    }

    protected void sendQuote(Quote quote) {
        mAPIFetch.post("jobs/" + mJob.getId() + "/quotes.json", quote.buildParams(), new APIResponseHandler(getActivity(), getActivity().getSupportFragmentManager(), true) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    mJob = new Job(response);
                    mLab.setJob(mJob).saveJob();
                    sendResult(Activity.RESULT_OK, mJob);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                switch (statusCode) {
                    case 422:
                        try {
                            JSONObject errors = errorResponse.getJSONObject("errors");
                            setErrors(errors);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void sendResult(int resultCode, Job job) {
        if (getTargetListener() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_JOB, job);

        getTargetListener().onResult(getRequestCode(), resultCode, intent);
    }

}
