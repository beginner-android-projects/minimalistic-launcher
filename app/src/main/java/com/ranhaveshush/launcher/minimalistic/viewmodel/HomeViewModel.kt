package com.ranhaveshush.launcher.minimalistic.viewmodel

import android.app.Application
import android.content.ComponentName
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ranhaveshush.launcher.minimalistic.launcher.AppsLauncher
import com.ranhaveshush.launcher.minimalistic.launcher.SettingsLauncher
import com.ranhaveshush.launcher.minimalistic.repository.HomeRepository
import com.ranhaveshush.launcher.minimalistic.vo.HomeApp
import com.ranhaveshush.launcher.minimalistic.vo.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val appsLauncher: AppsLauncher,
    private val settingsLauncher: SettingsLauncher
) : ViewModel() {
    val apps: StateFlow<Resource<List<HomeApp>>> = repository.listApps().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Resource.loading()
    )

    fun launchApp(context: Application, app: HomeApp) {
        val componentName = ComponentName(app.packageName, app.activityName)
        appsLauncher.launch(context, componentName)
    }

    fun launchAppDetails(context: Application, app: HomeApp): Boolean {
        return settingsLauncher.launchAppDetails(context, app.packageName)
    }

    fun launchWallpaperChooser(context: Application): Boolean {
        return settingsLauncher.launchWallpaperChooser(context)
    }
}
