package com.gruporaido.tasker_partner.util;

import javax.inject.Singleton;

import dagger.Component;

import com.gruporaido.tasker_library.util.ApplicationComponent;
import com.gruporaido.tasker_partner.TaskerPartner;
import com.gruporaido.tasker_partner.activity.BankAccountActivity;
import com.gruporaido.tasker_partner.activity.LoginActivity;
import com.gruporaido.tasker_partner.activity.MainActivity;
import com.gruporaido.tasker_partner.activity.SplashActivity;
import com.gruporaido.tasker_partner.fragment.AvailableFragment;
import com.gruporaido.tasker_partner.fragment.JobIncomingFragment;
import com.gruporaido.tasker_partner.fragment.JobQuoteAcceptanceFragment;
import com.gruporaido.tasker_partner.fragment.JobQuotingFormFragment;
import com.gruporaido.tasker_partner.fragment.JobQuotingFragment;
import com.gruporaido.tasker_partner.fragment.JobWaitingFragment;
import com.gruporaido.tasker_partner.fragment.JobWorkingFragment;
import com.gruporaido.tasker_partner.fragment.LoginFragment;
import com.gruporaido.tasker_partner.fragment.QuoteFormFragment;
import com.gruporaido.tasker_partner.fragment.RequestFragment;
import com.gruporaido.tasker_partner.receiver.NotificationBroadcastReceiver;
import com.gruporaido.tasker_partner.service.LocationManagerService;

@Singleton
@Component(modules = {TaskerPartnerModule.class})
public interface TaskerPartnerComponent extends ApplicationComponent {
    void inject(TaskerPartner application);

    void inject(SplashActivity activity);

    void inject(MainActivity activity);

    void inject(LoginActivity activity);

    void inject(LocationManagerService service);

    void inject(LoginFragment fragment);

    void inject(JobWaitingFragment fragment);

    void inject(AvailableFragment fragment);

    void inject(RequestFragment fragment);

    void inject(JobIncomingFragment fragment);

    void inject(JobQuotingFragment fragment);

    void inject(JobQuotingFormFragment fragment);

    void inject(QuoteFormFragment fragment);

    void inject(JobQuoteAcceptanceFragment fragment);

    void inject(JobWorkingFragment fragment);

    void inject(NotificationBroadcastReceiver receiver);

    void inject(BankAccountActivity bankAccountActivity);
}