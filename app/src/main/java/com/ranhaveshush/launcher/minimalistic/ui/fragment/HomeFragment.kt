package com.ranhaveshush.launcher.minimalistic.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ranhaveshush.launcher.minimalistic.R
import com.ranhaveshush.launcher.minimalistic.databinding.FragmentHomeBinding
import com.ranhaveshush.launcher.minimalistic.ktx.application
import com.ranhaveshush.launcher.minimalistic.ktx.launchRepeatOnStarted
import com.ranhaveshush.launcher.minimalistic.ui.adapter.HomeAppsAdapter
import com.ranhaveshush.launcher.minimalistic.ui.listener.HomeAppItemClickListener
import com.ranhaveshush.launcher.minimalistic.ui.listener.HomeAppItemLongClickListener
import com.ranhaveshush.launcher.minimalistic.viewmodel.HomeViewModel
import com.ranhaveshush.launcher.minimalistic.vo.HomeApp
import com.ranhaveshush.launcher.minimalistic.vo.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

/**
 * This home fragment represents the home screen,
 * a container for the favorite fast access apps.
 */
@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), HomeAppItemClickListener, HomeAppItemLongClickListener {
    private val viewModel: HomeViewModel by viewModels()

    private val appsAdapter = HomeAppsAdapter(this, this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHomeBinding.inflate(inflater)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel

        binding.recyclerViewApps.adapter = appsAdapter

        binding.root.setOnLongClickListener {
            viewModel.launchWallpaperChooser(application)
        }

        launchRepeatOnStarted {
            viewModel.apps.collect { resource ->
                if (resource.state.status == Resource.Status.SUCCESS) {
                    appsAdapter.submitList(resource.data)
                }
            }
        }

        return binding.root
    }

    override fun onAppClick(app: HomeApp) = viewModel.launchApp(application, app)

    override fun onAppLongClick(app: HomeApp) = viewModel.launchAppDetails(application, app)
}
