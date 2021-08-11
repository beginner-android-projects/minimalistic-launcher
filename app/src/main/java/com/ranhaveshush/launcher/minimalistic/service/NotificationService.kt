package com.ranhaveshush.launcher.minimalistic.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.ranhaveshush.launcher.minimalistic.data.notification.NotificationDataSource
import com.ranhaveshush.launcher.minimalistic.notification.ExcludedApps
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService : NotificationListenerService() {
    @Inject
    lateinit var dataSource: NotificationDataSource

    @Inject
    lateinit var excludedApps: ExcludedApps

    private lateinit var serviceScope: CoroutineScope

    override fun onListenerConnected() {
        serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    }

    override fun onListenerDisconnected() {
        if (this::serviceScope.isInitialized) {
            serviceScope.launch {
                dataSource.clear()
                this.cancel()
            }
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn?.let {
            if (shouldInclude(sbn)) {
                serviceScope.launch {
                    dataSource.add(sbn)
                    cancelNotification(sbn.key)
                }
            }
        }
    }

    private fun shouldInclude(sbn: StatusBarNotification): Boolean {
        return !sbn.isOngoing && sbn.isClearable && !excludedApps.isExcluded(sbn.packageName)
    }
}
