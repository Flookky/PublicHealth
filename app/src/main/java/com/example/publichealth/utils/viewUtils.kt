package com.example.publichealth.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.publichealth.R
import com.google.android.material.snackbar.Snackbar

fun Context.toast(message: String){
    Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
}

fun Context.toastLong(message: String){
    Toast.makeText(this,message,Toast.LENGTH_LONG).show()
}

fun View.snackbar(message: String){
    Snackbar.make(this,message,Snackbar.LENGTH_SHORT).setAction("Done",UndoListener()).show()
}