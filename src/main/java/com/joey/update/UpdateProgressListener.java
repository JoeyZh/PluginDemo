package com.joey.update;

public interface UpdateProgressListener {
        void onProgress(long current, long total);

        void onStartProgress();

        void onFinish();

        void onError();
    }