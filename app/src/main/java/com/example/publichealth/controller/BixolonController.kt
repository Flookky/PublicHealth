package com.example.publichealth.controller

import android.content.Context
import android.graphics.Bitmap
import com.bxl.config.editor.BXLConfigLoader
import com.example.publichealth.R
import com.example.publichealth.utils.Printer.Bixolon.BixolonPrinter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BixolonController(printerContext: Context): Printers() {
    private var context: Context = printerContext
    private var bxl: BixolonPrinter = BixolonPrinter(context)
    private val printerBrand = "Bixolon"
    val usb = BXLConfigLoader.DEVICE_BUS_USB
    //val lan = BXLConfigLoader.DEVICE_BUS_ETHERNET
    //val wifi = BXLConfigLoader.DEVICE_BUS_WIFI
    //val bluetooth = BXLConfigLoader.DEVICE_BUS_BLUETOOTH
    val srp330ii = BXLConfigLoader.PRODUCT_NAME_SRP_330II

    override fun isConnect(): Boolean {
        return isReady().first
    }

    override suspend fun printImage(bitmap: Bitmap): Boolean {
        return if (withContext(Dispatchers.Default) {
                bxl.printImage(bitmap,400,BixolonPrinter.ALIGNMENT_CENTER,1,0)
            }) {
                cutPaper()
            true
        } else {
            false
        }
    }

    override suspend fun connectUsb(portType: Int, model: String): Boolean {
        return if (withContext(Dispatchers.Default) {
                bxl.printerOpen(portType, model, "", false)
            }){
            super.brand = printerBrand
            super.model = model
            super.portType = portType
            super.printerType = "Thermal-Printer"
            true
        } else {
            false
        }
    }

    override suspend fun connectLan(portType: Int, model: String, ipAddress: String): Boolean {
        return if (withContext(Dispatchers.Default) {
                bxl.printerOpen(portType, model, ipAddress, false)
            }) {
            super.brand = printerBrand
            super.model = model
            super.portType = portType
            super.printerType = "Thermal-Printer"
            super.ipAddress = ipAddress
            true
        } else {
            false
        }
    }

    override suspend fun disconnect(): Boolean {
        return if (checkPrinterDetail()){
            if (isConnect()) {
                return withContext(Dispatchers.Default) {
                    bxl.printerClose()
                }
            } else {
                false
            }
        } else {
            false
        }
    }

    override fun cutPaper(){
        //feedPaper()
        bxl.cutPaper()
    }

    override fun feedPaper() {
        bxl.formFeed()
    }

    override fun getStatus(): String {
        return bxl.printerStatus
    }

    override fun isReady(): Pair<Boolean,String> {
        return when (getStatus()) {
            "StatusUpdate : Power on" -> {
                Pair(true,context.getString(R.string.printer_ready))
            }
            "StatusUpdate : Receipt Paper Empty" -> {
                Pair(false,context.getString(R.string.printer_no_paper))
            }
            else -> {
                Pair(false,context.getString(R.string.printer_error))
            }
        }
    }
}