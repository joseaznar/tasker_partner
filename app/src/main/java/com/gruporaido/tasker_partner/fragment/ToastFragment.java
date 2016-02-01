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

package com.gruporaido.tasker_partner.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gruporaido.tasker_library.fragment.BaseFragment;
import com.gruporaido.tasker_partner.R;

public class ToastFragment extends BaseFragment {

    public static final String EXTRA_CONTENT = "com.gruporaido.tasker.extra_content";

    private String mContent;
    private TextView mContentTextView;

    public static ToastFragment newInstance(String content) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_CONTENT, content);

        ToastFragment fragment = new ToastFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContent = getArguments().getString(EXTRA_CONTENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_toast, parent, false);

        mContentTextView = (TextView) view.findViewById(R.id.toast_contentTextView);
        mContentTextView.setText(mContent);

        return view;
    }

}
