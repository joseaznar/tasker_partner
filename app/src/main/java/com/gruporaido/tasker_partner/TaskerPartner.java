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

package com.gruporaido.tasker_partner;

import com.gruporaido.tasker_library.TaskerLibrary;
import com.gruporaido.tasker_partner.util.DaggerTaskerPartnerComponent;
import com.gruporaido.tasker_partner.util.TaskerPartnerModule;

public class TaskerPartner extends TaskerLibrary {

    @Override
    protected void createComponent() {
        mComponent = DaggerTaskerPartnerComponent.builder()
                .taskerPartnerModule(new TaskerPartnerModule(this))
                .build();
        mComponent.inject(this);
    }
}
