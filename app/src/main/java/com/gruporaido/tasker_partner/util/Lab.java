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

package com.gruporaido.tasker_partner.util;

import android.content.Context;

import com.gruporaido.tasker_library.model.BankAccount;
import com.gruporaido.tasker_library.model.JSONSerializer;
import com.gruporaido.tasker_library.model.Job;
import com.gruporaido.tasker_library.model.Phone;
import com.gruporaido.tasker_library.model.Request;
import com.gruporaido.tasker_library.model.User;
import com.gruporaido.tasker_library.report.Logging;
import com.gruporaido.tasker_library.report.SentryReporter;

// TODO change serializers and objets to ArrayList and key extraction
public class Lab {
    private static final String TAG = "Lab";
    protected final String USER_FILENAME = "user.json";
    protected final String JOB_FILENAME = "job.json";
    protected final String REQUEST_FILENAME = "request.json";
    protected final String PHONE_FILENAME = "phone.json";
    protected final String BANCKACCOUNT_FILENAME = "bankAccount.json";

    protected BankAccount mBankAccount;
    protected User mUser;
    protected Job mJob;
    protected Request mRequest;
    protected Phone mPhone;

    protected JSONSerializer mBankAccountSerializer;
    protected JSONSerializer mUserSerializer;
    protected JSONSerializer mJobSerializer;
    protected JSONSerializer mRequestSerializer;
    protected JSONSerializer mPhoneSerializer;

    protected Context mAppContext;

    Lab(Context appContext) {
        mAppContext = appContext;

        mUserSerializer = new JSONSerializer(appContext, USER_FILENAME);
        try {
            mUser = (User) mUserSerializer.loadObject("com.gruporaido.tasker_library.model.User");
        } catch (Exception e) {
            Logging.getLogger(SentryReporter.class).error(TAG, "Error loading user: ", e);
        } finally {
            if (mUser == null) {
                mUser = new User();
            }
        }

        mBankAccountSerializer = new JSONSerializer(appContext, BANCKACCOUNT_FILENAME);
        try {
            mBankAccount = (BankAccount) mBankAccountSerializer.loadObject("com.gruporaido.tasker_library.model.BankAccount");
        } catch (Exception e) {
            Logging.getLogger(SentryReporter.class).error(TAG, "Error loading bank account: ", e);
        } finally {
            if (mBankAccount == null) {
                mBankAccount = new BankAccount();
            }
        }

        mJobSerializer = new JSONSerializer(appContext, JOB_FILENAME);
        try {
            mJob = (Job) mJobSerializer.loadObject("com.gruporaido.tasker_library.model.Job");
        } catch (Exception e) {
            Logging.getLogger(SentryReporter.class).error(TAG, "Error loading job: ", e);
        } finally {
            if (mJob == null) {
                mJob = new Job();
            }
        }

        mRequestSerializer = new JSONSerializer(appContext, REQUEST_FILENAME);
        try {
            mRequest = (Request) mRequestSerializer.loadObject("com.gruporaido.tasker_library.model.Request");
        } catch (Exception e) {
            Logging.getLogger(SentryReporter.class).error(TAG, "Error loading request: ", e);
        } finally {
            if (mRequest == null) {
                mRequest = new Request();
            }
        }

        mPhoneSerializer = new JSONSerializer(appContext, PHONE_FILENAME);
        try {
            mPhone = (Phone) mPhoneSerializer.loadObject("com.gruporaido.tasker_library.model.Phone");
        } catch (Exception e) {
            Logging.getLogger(SentryReporter.class).error(TAG, "Error loading request: ", e);
        } finally {
            if (mPhone == null) {
                mPhone = new Phone();
            }
        }
    }

    public User getUser() {
        return mUser;
    }

    public Lab setUser(User user) {
        mUser = user;
        return this;
    }

    public BankAccount getBankAccount() {
        return mBankAccount;
    }

    public Lab setBankAccount(BankAccount bankAccount) {
        mBankAccount = bankAccount;
        return this;
    }

    public Job getJob() {
        return mJob;
    }

    public Lab setJob(Job job) {
        mJob = job;
        return this;
    }

    public Phone getPhone() {
        return mPhone;
    }

    public Lab setPhone(Phone phone) {
        mPhone = phone;
        return this;
    }

    public Request getRequest() {
        return mRequest;
    }

    public Lab setRequest(Request request) {
        mRequest = request;
        return this;
    }

    public boolean saveUser() {
        try {
            mUserSerializer.saveObject(mUser);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser() {
        try {
            mUserSerializer.deleteObject();
            mUser = new User();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveBankAccount() {
        try {
            mBankAccountSerializer.saveObject(mBankAccount);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBankAccount() {
        try {
            mBankAccountSerializer.deleteObject();
            mBankAccount = new BankAccount();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveJob() {
        try {
            mJobSerializer.saveObject(mJob);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteJob() {
        try {
            mJobSerializer.deleteObject();
            mJob = new Job();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveRequest() {
        try {
            mRequestSerializer.saveObject(mRequest);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRequest() {
        try {
            mRequestSerializer.deleteObject();
            mRequest = new Request();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean savePhone() {
        try {
            mPhoneSerializer.saveObject(mPhone);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePhone() {
        try {
            mPhoneSerializer.deleteObject();
            mPhone = new Phone();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
