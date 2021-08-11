package com.ranhaveshush.launcher.minimalistic.repository

import android.content.pm.ApplicationInfo
import com.ranhaveshush.launcher.minimalistic.data.app.HomeAppTransformer
import com.ranhaveshush.launcher.minimalistic.data.app.InstalledAppsDataSource
import com.ranhaveshush.launcher.minimalistic.vo.HomeApp
import com.ranhaveshush.launcher.minimalistic.vo.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val MAX_APPS = 7

class HomeRepository @Inject constructor(
    private val dataSource: InstalledAppsDataSource,
    private val dataTransformer: HomeAppTransformer
) {
    fun listApps(): Flow<Resource<List<HomeApp>>> = dataSource.asFlow().map { resolveInfos ->
        val appNamesFilter = Regex("(chrome|dialer|calendar|maps|photos|camera)")

        val systemApps = resolveInfos.filter { resolveInfo ->
            val applicationInfo = resolveInfo.activityInfo.applicationInfo
            applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0 && applicationInfo.packageName.contains(
                appNamesFilter
            )
        }

        val apps = systemApps.map { resolveInfo ->
            dataTransformer.transform(resolveInfo)
        }

        val filteredApps = if (apps.size > MAX_APPS) {
            apps.subList(0, MAX_APPS)
        } else {
            apps
        }

        val sortedApps = filteredApps.sortedBy { app ->
            app.name
        }

        Resource.success(sortedApps)
    }
}
