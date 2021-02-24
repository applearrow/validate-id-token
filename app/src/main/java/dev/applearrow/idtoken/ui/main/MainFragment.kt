package dev.applearrow.idtoken.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dev.applearrow.idtoken.databinding.MainFragmentBinding
import dev.applearrow.idtoken.ui.showError

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var binding: MainFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        return binding.root
    }

    private fun setListeners() {

        binding.otConfig.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToConfigFragment())
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()

        viewModel.intMsg.observe(viewLifecycleOwner) { resId ->
            if (resId != 0) {
                binding.message.text = getString(resId)
            }
        }

        viewModel.intError.observe(viewLifecycleOwner) { resId ->
            if (resId != 0) {
                binding.root.showError(getString(resId))
                viewModel.hideError()
            }
        }
    }

}