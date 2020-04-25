package com.ranhaveshush.launcher.minimalistic.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ranhaveshush.launcher.minimalistic.R
import com.ranhaveshush.launcher.minimalistic.databinding.FragmentAppDrawerBinding
import com.ranhaveshush.launcher.minimalistic.launcher.AppsLauncher
import com.ranhaveshush.launcher.minimalistic.launcher.SettingsLauncher
import com.ranhaveshush.launcher.minimalistic.ui.adapter.DrawerAppsAdapter
import com.ranhaveshush.launcher.minimalistic.ui.listener.DrawerAppItemClickListener
import com.ranhaveshush.launcher.minimalistic.ui.listener.DrawerAppItemLongClickListener
import com.ranhaveshush.launcher.minimalistic.util.InjectorUtils
import com.ranhaveshush.launcher.minimalistic.viewmodel.AppDrawerViewModel
import com.ranhaveshush.launcher.minimalistic.vo.DrawerAppItem
import com.ranhaveshush.launcher.minimalistic.vo.Resource.Status
import kotlinx.android.synthetic.main.fragment_app_drawer.*

/**
 * This app drawer fragment represents the all apps screen,
 * a container for all installed apps.
 */
class AppDrawerFragment : Fragment(R.layout.fragment_app_drawer), DrawerAppItemClickListener, DrawerAppItemLongClickListener {
    private val viewModel: AppDrawerViewModel by viewModels {
        InjectorUtils().provideAppDrawerViewModelFactory(packageManager)
    }

    private val appsAdapter = DrawerAppsAdapter(this, this)

    private val appsLauncher = AppsLauncher()

    private val settingsLauncher = SettingsLauncher()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAppDrawerBinding.inflate(layoutInflater)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel

        binding.recyclerViewApps.adapter = appsAdapter
        binding.recyclerViewApps.layoutManager = LinearLayoutManager(context).apply {
            stackFromEnd = true
        }

        viewModel.apps.observe(viewLifecycleOwner, Observer {
            if (it.state.status == Status.SUCCESS) {
                appsAdapter.submitList(it.data)
            }
        })

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        recyclerView_apps.smoothScrollToPosition(0)
    }

    override fun onAppClick(appItem: DrawerAppItem) = appsLauncher.launch(application, appItem.packageName)

    override fun onAppLongClick(appItem: DrawerAppItem) = settingsLauncher.launchAppDetails(application, appItem.packageName)
}
