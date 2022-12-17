package com.example.publichealth.ui.DialogFragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.example.publichealth.R
import com.example.publichealth.databinding.DialogProgressBinding

class Progress: DialogFragment() {
    private val binding: DialogProgressBinding by lazy { DialogProgressBinding.inflate(layoutInflater) }
    private lateinit var myContext: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(STYLE_NO_TITLE, R.style.progressDialogTheme)
        dialog?.setCancelable(false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.decorView?.systemUiVisibility
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawableResource(R.color.transparent)

        return super.onCreateDialog(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context
    }

}