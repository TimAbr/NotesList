package com.example.noteslist.presentation.settings

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.noteslist.databinding.FragmentSettingsBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.math.roundToInt

@AndroidEntryPoint
class SettingsBottomSheetFragment : BottomSheetDialogFragment() {

    private val viewModel: SettingsViewModel by viewModels()

    private var _binding: FragmentSettingsBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeSettings()
        setupListeners()
    }

    private fun observeSettings() {
        viewModel.settings
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { settings ->
                with(binding) {
                    val rawSpacing = settings.stackSpacing.toFloat()
                        .coerceIn(sliderSpacing.valueFrom, sliderSpacing.valueTo)
                    
                    val stepSize = sliderSpacing.stepSize
                    val spacingValue = if (stepSize > 0) {
                        (rawSpacing / stepSize).roundToInt() * stepSize
                    } else {
                        rawSpacing
                    }
                    
                    sliderSpacing.value = spacingValue

                    if (spacingValue.toInt() != settings.stackSpacing) {
                        viewModel.saveSettings(
                            spacing = spacingValue.toInt(),
                            maxVisible = settings.stackMaxVisible
                        )
                    }

                    val maxVisibleValue = settings.stackMaxVisible.toFloat()
                        .coerceIn(sliderMaxVisible.valueFrom, sliderMaxVisible.valueTo)
                    sliderMaxVisible.value = maxVisibleValue
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setupListeners() {
        with(binding) {
            btnSave.setOnClickListener {
                viewModel.saveSettings(
                    spacing = sliderSpacing.value.toInt(),
                    maxVisible = sliderMaxVisible.value.toInt()
                )
                dismiss()
            }

            btnReset.setOnClickListener {
                viewModel.reset()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
