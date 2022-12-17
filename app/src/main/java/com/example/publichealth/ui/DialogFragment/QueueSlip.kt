package com.example.publichealth.ui.DialogFragment

import android.app.Dialog
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.example.publichealth.R
import com.example.publichealth.databinding.DialogQueueSlipBinding
import com.example.publichealth.utils.Printer.BitmapUtils
import java.text.SimpleDateFormat
import java.util.*

class QueueSlip: DialogFragment() {
    private val binding: DialogQueueSlipBinding by lazy { DialogQueueSlipBinding.inflate(layoutInflater) }
    private lateinit var myContext: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return binding.root
    }

    private fun setQueueNumber(queueCount: Int){
        binding.queueNum.text = String.format("%03d",queueCount)
    }

    private fun setQueueDate(): String{
        val currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        binding.date.text = getString(R.string.queue_date,currentDate)

        return currentDate
    }

    fun getQueueBitmap(queueCount: Int): Bitmap{
        setQueueNumber(queueCount)
        setQueueDate()

        return loadBitmapFromView(binding.constraintQueue.rootView)
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
        c.drawBitmap(bmpOriginal, 0f, 0f, paint)
        return BitmapUtils.convertGreyImgByFloyd(bmpGrayscale)
    }

    private fun loadBitmapFromView(view: View): Bitmap {
        view.measure(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        val b: Bitmap = Bitmap.createBitmap(
            view.measuredWidth,
            view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(b)
        val a = view.measuredWidth
        val b1 = view.measuredHeight
        view.layout(0, 0, a, b1)
        view.draw(canvas)

        return b

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context
    }

}