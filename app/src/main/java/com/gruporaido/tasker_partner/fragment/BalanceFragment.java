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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.gruporaido.tasker_partner.R;
import com.gruporaido.tasker_library.fragment.BaseFragment;
import com.gruporaido.tasker_library.model.User;
import com.gruporaido.tasker_library.util.ApplicationComponent;
import com.gruporaido.tasker_partner.R;
import com.gruporaido.tasker_partner.util.TaskerPartnerComponent;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BalanceFragment extends BaseFragment {

    private static final String TAG = "BalanceFragment";
    private static final String EXTRA_BALANCE = "com.gruporaido.tasker.balance";

    @Bind(R.id.balance_balanceTextView)
    protected TextView mBalanceTextView;

    private Double mBalance;

    public static BalanceFragment newInstance(Double balance) {
        Bundle arguments = new Bundle();
        arguments.putDouble(EXTRA_BALANCE, balance);

        BalanceFragment fragment = new BalanceFragment();
        fragment.setArguments(arguments);

        return fragment;
    }
    public void injectFragment(ApplicationComponent component) {
        ((TaskerPartnerComponent) component).inject(this);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBalance = getArguments().getDouble(EXTRA_BALANCE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_balance, parent, false);
        ButterKnife.bind(this,view);

        mBalanceTextView = (TextView) view.findViewById(R.id.balance_balanceTextView);
        mBalanceTextView.setText(mBalance.toString());

        return view;
    }
}
