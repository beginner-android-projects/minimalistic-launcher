package com.ranhaveshush.launcher.minimalistic.viewmodel

import android.app.Application
import android.content.ComponentName
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.ranhaveshush.launcher.minimalistic.launcher.AppsLauncher
import com.ranhaveshush.launcher.minimalistic.launcher.SettingsLauncher
import com.ranhaveshush.launcher.minimalistic.repository.AppDrawerRepository
import com.ranhaveshush.launcher.minimalistic.ui.item.DrawerAppItem
import com.ranhaveshush.launcher.minimalistic.vo.DrawerApp
import com.ranhaveshush.launcher.minimalistic.vo.DrawerAppHeader
import com.ranhaveshush.launcher.minimalistic.vo.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class AppDrawerViewModel @Inject constructor(
    private val repository: AppDrawerRepository,
    private val appsLauncher: AppsLauncher,
    private val settingsLauncher: SettingsLauncher
) : ViewModel() {
    val searchQuery = MutableLiveData<String>()

    val apps: LiveData<Resource<List<DrawerAppItem>>> = searchQuery.switchMap {
        listApps(it)
    }

    init {
        clearSearchQuery()
    }

    fun clearSearchQuery() {
        searchQuery.value = ""
    }

    fun launchApp(context: Application, app: DrawerApp) {
        val componentName = ComponentName(app.packageName, app.activityName)
        appsLauncher.launch(context, componentName)
    }

    fun launchAppDetails(context: Application, app: DrawerApp): Boolean {
        return settingsLauncher.launchAppDetails(context, app.packageName)
    }

    private fun listApps(searchQuery: String): LiveData<Resource<List<DrawerAppItem>>> = liveData {
        val resource = repository.listApps(searchQuery).map { resource ->
            val apps = resource.data

            if (apps == null) {
                Resource.empty()
            } else {
                val drawerAppItems = if (searchQuery.isEmpty()) {
                    val appsCount = apps.size.toString()
                    mutableListOf(
                        DrawerAppItem(DrawerAppItem.Type.HEADER, DrawerAppHeader(appsCount))
                    )
                } else {
                    mutableListOf<DrawerAppItem>()
                }
                apps.mapTo(drawerAppItems) { app ->
                    DrawerAppItem(DrawerAppItem.Type.ENTRY, app)
                }
                Resource.success(drawerAppItems as List<DrawerAppItem>)
            }
        }.asLiveData(Dispatchers.Default)

        emitSource(resource)
    }
}
