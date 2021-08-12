package com.ranhaveshush.launcher.minimalistic.ktx

import android.app.Application
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

val Fragment.application: Application
    get() = requireActivity().application

val Fragment.packageManager: PackageManager
    get() = requireContext().packageManager

fun Fragment.launchRepeatOnStarted(
    block: suspend CoroutineScope.() -> Unit
): Job = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
    viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED, block)
}