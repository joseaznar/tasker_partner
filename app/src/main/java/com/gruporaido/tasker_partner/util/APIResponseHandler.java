/**
 *
 * GRUPO RAIDO CONFIDENTIAL
 * __________________
 *
 *  [2015] - [2015] Grupo Raido SAPI de CV
 *  All Rights Reserved.
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

package com.gruporaido.tasker_partner.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.gruporaido.tasker_library.http.ErrorHandler;
import com.gruporaido.tasker_partner.R;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public class APIResponseHandler extends JsonHttpResponseHandler {

    private static final String TAG = "APIResponseHandler";

    private Context mContext;
    private FragmentManager mFragmentManager;
    private ProgressDialog mProgressDialog;
    private boolean mShowProgress;

    public APIResponseHandler(Context context, FragmentManager fragmentManager, boolean showProgress) {
        super();
        mContext = context;
        mFragmentManager = fragmentManager;
        mShowProgress = showProgress;
        if (mShowProgress) {
            mProgressDialog = ProgressDialog.show(context, null, context.getString(R.string.loading_content));
        }
    }

    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        Log.i(TAG, "State code: " + statusCode + ", response: " + response);
        onFinish(true);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        Log.i(TAG, "State code: " + statusCode + ", response: " + response);
        onFinish(true);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        ErrorHandler.handleError(mFragmentManager, statusCode, errorResponse.toString());
        onFinish(true);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        ErrorHandler.handleError(mFragmentManager, statusCode, responseString);
        onFinish(true);
    }

    public void onFinish(boolean success) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
}
