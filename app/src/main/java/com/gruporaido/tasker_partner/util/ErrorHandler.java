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

import android.support.v4.app.FragmentManager;
import android.widget.RadioButton;

import com.gruporaido.tasker_library.fragment.ErrorFragment;
import com.gruporaido.tasker_library.report.Logging;
import com.gruporaido.tasker_library.report.SentryReporter;
import com.rey.material.widget.EditText;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ErrorHandler {

    private static final String TAG = "ErrorHandler";

    public static String buildErrorString(JSONArray array) {
        String errors = "";
        for (int i = 0; i < array.length(); ++i) {
            try {
                errors += array.getString(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (i < array.length() - 1) {
                errors += ", ";
            }
        }
        return errors;
    }

    public static void handleError(FragmentManager fragmentManager, int statusCode, String message) {
        if (message == null) {
            message = "Unknown error";
        }
        ErrorFragment fragment = ErrorFragment.newInstance(message);
        try {
            fragment.show(fragmentManager, ErrorFragment.DIALOG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void handleError(FragmentManager fragmentManager, int statusCode, JSONObject errorResponse) {
        if (statusCode >= 400 && statusCode < 500) {
            JSONObject error = null;
            try {
                error = errorResponse.getJSONObject("error");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (error != null) {
                try {
                    handleError(fragmentManager, statusCode, error.getString("message"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (statusCode == 0) {
            handleError(fragmentManager, statusCode, "No se pudo contactar al servidor. Intente más tarde.");
        } else {
            handleError(fragmentManager, statusCode, "Hubo un error desconocido en el servidor.");
        }
        Logging.getLogger(SentryReporter.class).error(TAG, "Error doing request: status: " + statusCode + ", response: " + errorResponse);
    }

    public static void addErrorsToEditText(EditText field, JSONObject errors, String key) throws JSONException {
        if (errors.has(key)) {
            field.setError(buildErrorString(errors.getJSONArray(key)));
        }
    }

    public static void addErrorsToRadioButton(RadioButton field, JSONObject errors, String key) throws JSONException {
        if (errors.has(key)) {
            field.setError(buildErrorString(errors.getJSONArray(key)));
        }
    }
}
