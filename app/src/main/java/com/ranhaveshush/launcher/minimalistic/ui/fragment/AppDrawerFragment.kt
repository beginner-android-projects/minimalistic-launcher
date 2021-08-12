package com.ranhaveshush.launcher.minimalistic.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ranhaveshush.launcher.minimalistic.R
import com.ranhaveshush.launcher.minimalistic.databinding.FragmentAppDrawerBinding
import com.ranhaveshush.launcher.minimalistic.ktx.application
import com.ranhaveshush.launcher.minimalistic.ktx.hideKeyboard
import com.ranhaveshush.launcher.minimalistic.ktx.launchRepeatOnStarted
import com.ranhaveshush.launcher.minimalistic.ui.adapter.DrawerAppsAdapter
import com.ranhaveshush.launcher.minimalistic.ui.listener.DrawerAppItemClickListener
import com.ranhaveshush.launcher.minimalistic.ui.listener.DrawerAppItemLongClickListener
import com.ranhaveshush.launcher.minimalistic.viewmodel.AppDrawerViewModel
import com.ranhaveshush.launcher.minimalistic.vo.DrawerApp
import com.ranhaveshush.launcher.minimalistic.vo.Resource.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_app_drawer.recyclerView_apps
import kotlinx.coroutines.flow.collect

/**
 * This app drawer fragment represents the all apps screen,
 * a container for all installed apps.
 */
@AndroidEntryPoint
class AppDrawerFragment : Fragment(R.layout.fragment_app_drawer), DrawerAppItemClickListener,
    DrawerAppItemLongClickListener {
    private val viewModel: AppDrawerViewModel by viewModels()

    private val appsAdapter = DrawerAppsAdapter(this, this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAppDrawerBinding.inflate(layoutInflater)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel

        binding.recyclerViewApps.adapter = appsAdapter
        binding.recyclerViewApps.layoutManager = LinearLayoutManager(context).apply {
            stackFromEnd = true
        }

        launchRepeatOnStarted {
            viewModel.apps.collect { resource ->
                if (resource.state.status == Status.SUCCESS) {
                    appsAdapter.submitList(resource.data)
                    recyclerView_apps.smoothScrollToPosition(0)
                }
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        viewModel.clearSearchQuery()
    }

    override fun onAppClick(app: DrawerApp) {
        viewModel.launchApp(application, app)
        viewModel.clearSearchQuery()

        requireContext().hideKeyboard(view)
    }

    override fun onAppLongClick(app: DrawerApp): Boolean {
        val launched = viewModel.launchAppDetails(application, app)
        viewModel.clearSearchQuery()
        return launched
    }
}
