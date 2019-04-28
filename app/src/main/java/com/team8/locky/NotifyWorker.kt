package com.team8.locky

import android.content.Context
import androidx.work.Operation.State.SUCCESS
import androidx.work.Worker
import androidx.work.WorkerParameters
import io.reactivex.annotations.NonNull


class NotifyWorker(@NonNull context: Context, @NonNull params: WorkerParameters) : Worker(context, params) {

    @NonNull
    override fun doWork(): Result {
        // Method to trigger an instant notification

        return Result.success()
        // (Returning RETRY tells WorkManager to try this task again
        // later; FAILURE says not to try again.)
    }
}