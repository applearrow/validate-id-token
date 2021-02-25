package dev.applearrow.idtoken.ui.main

import android.graphics.Color
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dev.applearrow.idtoken.R
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

    private fun onButtonClick(view: View): Unit {
        val action = when (view.id) {
            R.id.decodeToken -> MainFragmentDirections.actionMainFragmentToJsonFragment(
                DetailScreen.DECODED_TOKEN.name
            )
            else -> null
        }
        action?.let {
            findNavController().navigate(it)
        }
    }

    private fun setListeners() {

        binding.otConfig.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToConfigFragment())
        }

        binding.decodeToken.setOnClickListener(::onButtonClick)

        binding.validateToken.setOnClickListener {
            viewModel.validateToken()
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()

        viewModel.encodedTokenStr.observe(viewLifecycleOwner) { token ->
            val parts = token.split(".")
            val numParts = parts.size
            var ssb = SimpleSpanBuilder(parts[0], ForegroundColorSpan(Color.RED))
            if (numParts > 1) {
                ssb += SimpleSpanBuilder.Span(".", ForegroundColorSpan(Color.BLACK))
                ssb += SimpleSpanBuilder.Span(parts[1], ForegroundColorSpan(Color.MAGENTA))
                if (numParts > 2) {
                    ssb += SimpleSpanBuilder.Span(".", ForegroundColorSpan(Color.BLACK))
                    ssb += SimpleSpanBuilder.Span(parts[2], ForegroundColorSpan(Color.BLUE))
                }
            }
            binding.message.text = ssb.build()
        }

        viewModel.intMsg.observe(viewLifecycleOwner) { resId ->
            if (resId != 0) {
                binding.message.text = getString(resId)
            }
        }

        viewModel.strMsg.observe(viewLifecycleOwner) { msg ->
            if (msg.isNotBlank()) {
                binding.message.text = msg
            }
        }

        viewModel.intError.observe(viewLifecycleOwner) { resId ->
            if (resId != 0) {
                binding.root.showError(getString(resId))
                viewModel.hideError()
            }
        }

        viewModel.strError.observe(viewLifecycleOwner) { msg ->
            if (msg.isNotBlank()) {
                binding.root.showError(msg)
                viewModel.hideError()
            }
        }

        viewModel.intValidIcon.observe(viewLifecycleOwner) { resId ->
            if (resId != 0) {
                binding.isValid.setImageResource(resId)
            }
        }

    }

}