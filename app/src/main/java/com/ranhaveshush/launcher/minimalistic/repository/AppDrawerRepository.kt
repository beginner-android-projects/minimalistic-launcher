package com.ranhaveshush.launcher.minimalistic.repository

import android.util.Log
import com.ranhaveshush.launcher.minimalistic.data.app.DrawerAppTransformer
import com.ranhaveshush.launcher.minimalistic.data.app.InstalledAppsDataSource
import com.ranhaveshush.launcher.minimalistic.vo.DrawerApp
import com.ranhaveshush.launcher.minimalistic.vo.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppDrawerRepository @Inject constructor(
    private val dataSource: InstalledAppsDataSource,
    private val dataTransformer: DrawerAppTransformer
) {
    fun listApps(filter: String = ""): Flow<Resource<List<DrawerApp>>> = dataSource.asFlow().map { resolveInfos ->
        val apps = resolveInfos.toList().map { resolveInfo ->
            dataTransformer.transform(resolveInfo)
        }

        val normalizedFilter = filter.toLowerCase()
        val filteredApps = apps.filter { app ->
            app.name.startsWith(normalizedFilter)
        }

        val sortedApps = filteredApps.sortedBy { app ->
            app.name
        }

        Resource.success(sortedApps)
    }
}
