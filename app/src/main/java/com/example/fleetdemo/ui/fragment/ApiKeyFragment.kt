package com.example.fleetdemo.ui.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.fleetdemo.R
import java.lang.ClassCastException

class ApiKeyFragment(private val initialKey : String = "") : DialogFragment() {

    private lateinit var listener: ApiKeyDialogListener
    interface ApiKeyDialogListener {
        fun onApiKeyDialogPositiveClick(apiKey : String)
        fun onApiKeyDialogNegativeClick()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_api_key, null)
            val apiKeyEditText = layout.findViewById<EditText>(R.id.edit_api_key)
            apiKeyEditText.setText(initialKey)
            builder.setView(layout)
                .setMessage(R.string.dialog_api_enter_key)
                .setPositiveButton(R.string.dialog_api_set) { _, _ ->
                    listener.onApiKeyDialogPositiveClick(apiKeyEditText.text.toString())
                }
                .setNegativeButton(R.string.dialog_api_cancel) { _, _ ->
                    listener.onApiKeyDialogNegativeClick()
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as ApiKeyDialogListener
        } catch (e: ClassCastException){
            throw ClassCastException("$context must implement ApiKeyDialogListener")
        }
    }

}