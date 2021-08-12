package com.ranhaveshush.launcher.minimalistic.viewmodel

import android.app.Application
import android.content.ComponentName
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ranhaveshush.launcher.minimalistic.launcher.AppsLauncher
import com.ranhaveshush.launcher.minimalistic.launcher.SettingsLauncher
import com.ranhaveshush.launcher.minimalistic.repository.AppDrawerRepository
import com.ranhaveshush.launcher.minimalistic.ui.item.DrawerAppItem
import com.ranhaveshush.launcher.minimalistic.vo.DrawerApp
import com.ranhaveshush.launcher.minimalistic.vo.DrawerAppHeader
import com.ranhaveshush.launcher.minimalistic.vo.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppDrawerViewModel @Inject constructor(
    private val repository: AppDrawerRepository,
    private val appsLauncher: AppsLauncher,
    private val settingsLauncher: SettingsLauncher
) : ViewModel() {
    val searchQuery = MutableStateFlow("")

    val apps: StateFlow<Resource<List<DrawerAppItem>>> = searchQuery.flatMapLatest { searchQuery ->
        listApps(searchQuery)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Resource.loading()
    )

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

    private fun listApps(searchQuery: String): StateFlow<Resource<List<DrawerAppItem>>> =
        repository.listApps(searchQuery).map { resource ->
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
        }.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), Resource.loading()
        )
}
