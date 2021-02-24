package dev.applearrow.idtoken.ui.main

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import dev.applearrow.idtoken.databinding.JsonFragmentBinding
import dev.applearrow.idtoken.ui.showError

class JsonFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val args: JsonFragmentArgs by navArgs()

    private lateinit var binding: JsonFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = JsonFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.subtitle.text = args.data
        binding.message.typeface = Typeface.MONOSPACE

        when (args.data) {
            DetailScreen.DECODED_TOKEN.name -> viewModel.decodedTokenStr.observe(viewLifecycleOwner) { str ->
                binding.message.text = str
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