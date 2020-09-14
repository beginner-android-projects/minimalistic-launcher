package com.ranhaveshush.launcher.minimalistic.di

import android.content.pm.ResolveInfo
import com.ranhaveshush.launcher.minimalistic.data.app.InstalledAppsDataSource
import com.ranhaveshush.launcher.minimalistic.data.app.InstalledAppsDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import java.util.Collections

@Module
@InstallIn(ApplicationComponent::class)
class InstalledAppsDataModule {
    @Provides
    fun provideInstalledAppsCache(): MutableMap<String, ResolveInfo> {
        return Collections.synchronizedMap(HashMap())
    }

    @Provides
    fun provideInstalledAppsChannel(): BroadcastChannel<Collection<ResolveInfo>> {
        return BroadcastChannel(Channel.CONFLATED)
    }

    @Module
    @InstallIn(ApplicationComponent::class)
    abstract class InstalledAppsModuleDataSource {
        @Binds
        abstract fun bindInstalledAppsDataSource(
            source: InstalledAppsDataSourceImpl
        ): InstalledAppsDataSource
    }
}
