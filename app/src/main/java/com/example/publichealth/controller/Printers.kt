package com.example.publichealth.controller

import android.graphics.Bitmap

abstract class Printers {
    var brand = ""
    var model = ""
    var printerType = ""
    var portType = -1
    var ipAddress = ""

    private val fullName: String
        get() = "This printer brand is $brand model $model which has type:$printerType" +
                "and connect by $portType"

    init {
        checkPrinterDetail()
    }

    abstract fun isConnect(): Boolean
    abstract suspend fun printImage(bitmap: Bitmap): Boolean
    abstract suspend fun connectUsb(portType: Int, model: String): Boolean
    abstract suspend fun connectLan(portType: Int, model: String, ipAddress: String): Boolean
    abstract suspend fun disconnect(): Boolean
    abstract fun isReady(): Pair<Boolean,String>
    abstract fun cutPaper()
    abstract fun feedPaper()
    abstract fun getStatus(): String

    protected fun checkPrinterDetail(): Boolean{
        println("Initializing $fullName")
        if (brand.isEmpty()) {
            println("There is no Printer brand")
            return false
        }
        if (model.isEmpty()) {
            println("There is no Printer model")
            return false
        }
        if (printerType.isEmpty()) {
            println("There is no Printer printerType")
            return false
        }
        if (portType == -1) {
            println("There is no Printer portType")
            return false
        }
        return true
    }
}