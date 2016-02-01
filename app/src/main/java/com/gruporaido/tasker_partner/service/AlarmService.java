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

package com.gruporaido.tasker_partner.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;

import com.gruporaido.tasker_partner.R;


public class AlarmService extends Service implements MediaPlayer.OnCompletionListener {

    public static final String TAG = "AlarmService";

    MediaPlayer mMediaPlayer;
    Vibrator mVibrator;

    long mVibrationPattern[] = {0, 800, 200, 1200, 300, 2000, 400, 4000};


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmSound == null) {
            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            if (alarmSound == null) {
                alarmSound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_sound);
            }
        }
        mMediaPlayer = MediaPlayer.create(this, alarmSound);
        mMediaPlayer.setOnCompletionListener(this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
        if (mVibrator.hasVibrator()) {
            mVibrator.vibrate(mVibrationPattern, 0);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mVibrator.cancel();
        mMediaPlayer.release();
    }

    @Override
    public void onCompletion(MediaPlayer _mediaPlayer) {
        stopSelf();
    }

}