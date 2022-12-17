package com.example.publichealth.ui.Activity

import android.graphics.*
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import com.example.publichealth.R
import com.example.publichealth.controller.BixolonController
import com.example.publichealth.databinding.ActivityMainBinding
import com.example.publichealth.ui.BaseActivity
import com.example.publichealth.ui.DialogFragment.Progress
import com.example.publichealth.ui.DialogFragment.QueueSlip
import com.example.publichealth.utils.Printer.BitmapUtils
import com.example.publichealth.utils.SharePref
import com.example.publichealth.utils.toastLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : BaseActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var myPref: SharePref
    private lateinit var bixolon: BixolonController
    private lateinit var queueSlip: QueueSlip
    private lateinit var fm: FragmentManager
    private lateinit var progress: Progress
    private var queueCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initInstance()
    }

    private fun initInstance(){
        fm = this.supportFragmentManager
        myPref = SharePref(this)
        bixolon = BixolonController(this)
        setQueueDialog()
        setQueueText()
        onClickActivate()
    }

    private fun onClickActivate(){
        binding.callQueue.setOnClickListener {
            printQueue()
        }
        binding.clearQueue.setOnClickListener {
            resetQueueCount()
        }
    }

    private fun printQueue(){
        launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                progress = Progress()
                progress.show(fm,"progress")
                if (bixolon.isReady().first) {
                    queueCount += 1
                    myPref.addQueueStack(queueCount)
                    setQueueText()
                    //binding.testImg.setImageBitmap(bmp)
                    val bmp = getQueueBitmap(queueCount)
                    bixolon.printImage(bmp)
                    bixolon.printImage(bmp)
                    withContext(Dispatchers.Main) {
                        progress.dismiss()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        toastLong("เครื่องปรินท์ไม่พร้อมใช้งาน กรุณาลองใหม่อีกครั้ง")
                        println("Status Printer: ${bixolon.isReady().second}")
                        progress.dismiss()
                    }
                }

            }
        }
    }

    private fun resetQueueCount() {
        launch(Dispatchers.Main) {
            progress = Progress()
            progress.show(fm,"progress")
            delay(500)
            queueCount = 0
            myPref.clearQueueStack()
            setQueueText()
            progress.dismiss()
            toastLong("รีเซ็ตคิวทั้งหมดแล้ว")
        }
    }

    private fun setQueueDialog(){
        queueSlip = QueueSlip()
    }

    private fun connectPrinter(){
        progress = Progress()
        progress.show(fm,"progress")
        launch {
            if (bixolon.connectUsb(bixolon.usb,bixolon.srp330ii)) {
                withContext(Dispatchers.Main) {
                    toastLong("เชื่อมต่อ Printer สำเร็จ")
                    progress.dismiss()
                }
            } else {
                withContext(Dispatchers.Main) {
                    toastLong("เชื่อมต่อ Printer ไม่สำเร็จ กรุณาลองใหม่อีกครั้ง")
                    progress.dismiss()
                }
            }
        }
    }

    private fun setQueueText(){
        binding.queueCount.text = String.format("%03d",queueCount)
    }

    private fun setQueueNumber(queueCount: Int){
        binding.queueNum.text = String.format("%03d",queueCount)
    }

    private fun setQueueDate(): String{
        val currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        binding.date.text = getString(R.string.queue_date,currentDate)

        return currentDate
    }

    private fun getQueueBitmap(queueCount: Int): Bitmap{
        setQueueNumber(queueCount)
        setQueueDate()

        return toGrayscale(loadBitmapFromView(binding.constraintQueue.rootView))
    }

    private fun toGrayscale(bmpOriginal: Bitmap): Bitmap {
        val width: Int = bmpOriginal.width
        val height: Int = bmpOriginal.height
        val bmpGrayscale = Bitmap.createBitmap(
            width, height,
            Bitmap.Config.RGB_565
        )
        val c = Canvas(bmpGrayscale)
        val paint = Paint()
        val cm = ColorMatrix()
        cm.setSaturation(0f)
        val f = ColorMatrixColorFilter(cm)
        paint.colorFilter = f
        c.drawBitmap(bmpOriginal, 0f, -40f, paint)

        //return trimBitmap(BitmapUtils.convertGreyImgByFloyd(bmpGrayscale))
        return trimBitmap(bmpGrayscale)
    }

    private fun trimBitmap(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        var startWidth = 0
        var startHeight = 0

        //TRIM WIDTH - LEFT
        for (x in 0 until width) {
            if (startWidth == 0) {
                for (y in 0 until height) {
                    if (bitmap.getPixel(x, y) !== Color.TRANSPARENT) {
                        startWidth = x
                        break
                    }
                }
            } else break
        }


        //TRIM WIDTH - RIGHT


        //TRIM WIDTH - RIGHT
        var endWidth = 0
        for (x in width - 1 downTo 0) {
            if (endWidth == 0) {
                for (y in 0 until height) {
                    if (bitmap.getPixel(x, y) !== Color.TRANSPARENT) {
                        endWidth = x
                        break
                    }
                }
            } else break
        }

        //TRIM HEIGHT - TOP
        for (y in 0 until height) {
            if (startHeight == 0) {
                for (x in 0 until width) {
                    if (bitmap.getPixel(x, y) !== Color.TRANSPARENT) {
                        startHeight = y
                        break
                    }
                }
            } else break
        }

        //TRIM HEIGHT - BOTTOM
        var endHeight = 0
        for (y in height - 1 downTo 0) {
            if (endHeight == 0) {
                for (x in 0 until width) {
                    if (bitmap.getPixel(x, y) !== Color.TRANSPARENT) {
                        endHeight = y
                        break
                    }
                }
            } else break
        }

        println("startWidth is $startWidth")
        println("startHeight is $startHeight")
        println("endWidth is $endWidth")
        println("endHeight is $endHeight")

        return Bitmap.createBitmap(
            bitmap,
            startWidth,
            startHeight,
            endWidth - startWidth,
            endHeight - 80
        );
    }

    private fun loadBitmapFromView(view: View): Bitmap {
        binding.mainConstraint.visibility = View.GONE
        view.measure(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        val b: Bitmap = Bitmap.createBitmap(
            view.measuredWidth,
            view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )

        val a = view.measuredWidth
        val b1 = view.measuredHeight

        val canvas = Canvas(b)

        view.layout(0, 0, a, b1)
        view.draw(canvas)
        binding.mainConstraint.visibility = View.VISIBLE
        return b

    }

    override fun onResume() {
        super.onResume()
        queueCount = myPref.getQueueStack()
        setQueueText()
        connectPrinter()
    }

    //suspend fun test() = suspendCoroutine<> {  }

    override fun onDestroy() {
        launch {
            if (bixolon.disconnect()) {
                println("Close Printer successful")
            } else {
                println("Close Printer successful")
            }
        }
        super.onDestroy()
    }
}