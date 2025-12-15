package com.strikes.busgapp.eiorfk.presentation.ui.load

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.strikes.busgapp.MainActivity
import com.strikes.busgapp.R
import com.strikes.busgapp.databinding.FragmentLoadStrikeBudgetBinding
import com.strikes.busgapp.eiorfk.data.shar.StrikeBudgetSharedPreference
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class StrikeBudgetLoadFragment : Fragment(R.layout.fragment_load_strike_budget) {
    private lateinit var strikeBudgetLoadBinding: FragmentLoadStrikeBudgetBinding

    private val strikeBudgetLoadViewModel by viewModel<StrikeBudgetLoadViewModel>()

    private val strikeBudgetSharedPreference by inject<StrikeBudgetSharedPreference>()

    private var strikeBudgetUrl = ""

    private val strikeBudgetRequestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            strikeBudgetNavigateToSuccess(strikeBudgetUrl)
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                strikeBudgetSharedPreference.strikeBudgetNotificationRequest =
                    (System.currentTimeMillis() / 1000) + 259200
                strikeBudgetNavigateToSuccess(strikeBudgetUrl)
            } else {
                strikeBudgetNavigateToSuccess(strikeBudgetUrl)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        strikeBudgetLoadBinding = FragmentLoadStrikeBudgetBinding.bind(view)

        strikeBudgetLoadBinding.strikeBudgetGrandButton.setOnClickListener {
            val strikeBudgetPermission = Manifest.permission.POST_NOTIFICATIONS
            strikeBudgetRequestNotificationPermission.launch(strikeBudgetPermission)
            strikeBudgetSharedPreference.strikeBudgetNotificationRequestedBefore = true
        }

        strikeBudgetLoadBinding.strikeBudgetSkipButton.setOnClickListener {
            strikeBudgetSharedPreference.strikeBudgetNotificationRequest =
                (System.currentTimeMillis() / 1000) + 259200
            strikeBudgetNavigateToSuccess(strikeBudgetUrl)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                strikeBudgetLoadViewModel.strikeBudgetHomeScreenState.collect {
                    when (it) {
                        is StrikeBudgetLoadViewModel.StrikeBudgetHomeScreenState.StrikeBudgetLoading -> {

                        }

                        is StrikeBudgetLoadViewModel.StrikeBudgetHomeScreenState.StrikeBudgetError -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                        is StrikeBudgetLoadViewModel.StrikeBudgetHomeScreenState.StrikeBudgetSuccess -> {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                                val strikeBudgetPermission = Manifest.permission.POST_NOTIFICATIONS
                                val strikeBudgetPermissionRequestedBefore = strikeBudgetSharedPreference.strikeBudgetNotificationRequestedBefore

                                if (ContextCompat.checkSelfPermission(requireContext(), strikeBudgetPermission) == PackageManager.PERMISSION_GRANTED) {
                                    strikeBudgetNavigateToSuccess(it.data)
                                } else if (!strikeBudgetPermissionRequestedBefore && (System.currentTimeMillis() / 1000 > strikeBudgetSharedPreference.strikeBudgetNotificationRequest)) {
                                    // первый раз — показываем UI для запроса
                                    strikeBudgetLoadBinding.strikeBudgetNotiGroup.visibility = View.VISIBLE
                                    strikeBudgetLoadBinding.strikeBudgetLoadingGroup.visibility = View.GONE
                                    strikeBudgetUrl = it.data
                                } else if (shouldShowRequestPermissionRationale(strikeBudgetPermission)) {
                                    // временный отказ — через 3 дня можно показать
                                    if (System.currentTimeMillis() / 1000 > strikeBudgetSharedPreference.strikeBudgetNotificationRequest) {
                                        strikeBudgetLoadBinding.strikeBudgetNotiGroup.visibility = View.VISIBLE
                                        strikeBudgetLoadBinding.strikeBudgetLoadingGroup.visibility = View.GONE
                                        strikeBudgetUrl = it.data
                                    } else {
                                        strikeBudgetNavigateToSuccess(it.data)
                                    }
                                } else {
                                    // навсегда отклонено — просто пропускаем
                                    strikeBudgetNavigateToSuccess(it.data)
                                }
                            } else {
                                strikeBudgetNavigateToSuccess(it.data)
                            }
                        }

                        StrikeBudgetLoadViewModel.StrikeBudgetHomeScreenState.StrikeBudgetNotInternet -> {
                            strikeBudgetLoadBinding.strikeBudgetStateGroup.visibility = View.VISIBLE
                            strikeBudgetLoadBinding.strikeBudgetLoadingGroup.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun strikeBudgetNavigateToSuccess(data: String) {
        findNavController().navigate(
            R.id.action_strikeBudgetLoadFragment_to_strikeBudgetV,
            bundleOf(STRIKE_BUDGET_D to data)
        )
    }

    companion object {
        const val STRIKE_BUDGET_D = "strikeBudgetData"
    }
}