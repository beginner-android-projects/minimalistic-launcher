package com.ranhaveshush.launcher.minimalistic.viewmodel

import android.app.Application
import android.content.ComponentName
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.ranhaveshush.launcher.minimalistic.launcher.AppsLauncher
import com.ranhaveshush.launcher.minimalistic.launcher.SettingsLauncher
import com.ranhaveshush.launcher.minimalistic.repository.HomeRepository
import com.ranhaveshush.launcher.minimalistic.vo.HomeApp
import com.ranhaveshush.launcher.minimalistic.vo.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val appsLauncher: AppsLauncher,
    private val settingsLauncher: SettingsLauncher
) : ViewModel() {
    val apps: LiveData<Resource<List<HomeApp>>> = liveData {
        emitSource(repository.listApps().asLiveData(Dispatchers.Default))
    }

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
