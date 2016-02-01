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
import android.widget.TextView;

import com.gruporaido.tasker_partner.R;
import com.gruporaido.tasker_library.model.BankAccount;
import com.gruporaido.tasker_library.model.BankAccount;
import com.gruporaido.tasker_partner.R;
import com.gruporaido.tasker_partner.util.Lab;

import javax.inject.Inject;

import butterknife.Bind;

public class BankAccountFragment extends Fragment {

    private static final String TAG = "MoneyFragment";

    private static final String EXTRA_BANK_ACCOUNT = "com.gruporaido.tasker.extra_bank_account";

    @Inject
    protected Lab mLab;

    private BankAccount mBankAccount;

    @Bind(R.id.bank_account_codeTextView)
    protected TextView mCodeTextView;

    public static BankAccountFragment newInstance(BankAccount account) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(EXTRA_BANK_ACCOUNT, account);

        BankAccountFragment fragment = new BankAccountFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBankAccount = (BankAccount) getArguments().getSerializable(EXTRA_BANK_ACCOUNT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bank_account, parent, false);

        mCodeTextView = (TextView) view.findViewById(R.id.bank_account_codeTextView);
        mCodeTextView.setText(mBankAccount.getCode());

        return view;
    }
}