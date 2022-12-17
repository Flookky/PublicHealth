package com.example.publichealth.utils.Printer

import android.graphics.*

object BitmapUtils {
    private val p0 = intArrayOf(0, 128)
    private val p1 = intArrayOf(0, 64)
    private val p2 = intArrayOf(0, 32)
    private val p3 = intArrayOf(0, 16)
    private val p4 = intArrayOf(0, 8)
    private val p5 = intArrayOf(0, 4)
    private val p6 = intArrayOf(0, 2)
    private val Floyd16x16 = arrayOf(
        intArrayOf(0, 128, 32, 160, 8, 136, 40, 168, 2, 130, 34, 162, 10, 138, 42, 170),
        intArrayOf(192, 64, 224, 96, 200, 72, 232, 104, 194, 66, 226, 98, 202, 74, 234, 106),
        intArrayOf(48, 176, 16, 144, 56, 184, 24, 152, 50, 178, 18, 146, 58, 186, 26, 154),
        intArrayOf(240, 112, 208, 80, 248, 120, 216, 88, 242, 114, 210, 82, 250, 122, 218, 90),
        intArrayOf(12, 140, 44, 172, 4, 132, 36, 164, 14, 142, 46, 174, 6, 134, 38, 166),
        intArrayOf(204, 76, 236, 108, 196, 68, 228, 100, 206, 78, 238, 110, 198, 70, 230, 102),
        intArrayOf(60, 188, 28, 156, 52, 180, 20, 148, 62, 190, 30, 158, 54, 182, 22, 150),
        intArrayOf(252, 124, 220, 92, 244, 116, 212, 84, 254, 126, 222, 94, 246, 118, 214, 86),
        intArrayOf(3, 131, 35, 163, 11, 139, 43, 171, 1, 129, 33, 161, 9, 137, 41, 169),
        intArrayOf(195, 67, 227, 99, 203, 75, 235, 107, 193, 65, 225, 97, 201, 73, 233, 105),
        intArrayOf(51, 179, 19, 147, 59, 187, 27, 155, 49, 177, 17, 145, 57, 185, 25, 153),
        intArrayOf(243, 115, 211, 83, 251, 123, 219, 91, 241, 113, 209, 81, 249, 121, 217, 89),
        intArrayOf(15, 143, 47, 175, 7, 135, 39, 167, 13, 141, 45, 173, 5, 133, 37, 165),
        intArrayOf(207, 79, 239, 111, 199, 71, 231, 103, 205, 77, 237, 109, 197, 69, 229, 101),
        intArrayOf(63, 191, 31, 159, 55, 183, 23, 151, 61, 189, 29, 157, 53, 181, 21, 149),
        intArrayOf(254, 127, 223, 95, 247, 119, 215, 87, 253, 125, 221, 93, 245, 117, 213, 85)
    )

    fun parseBmpToByte(bitmap: Bitmap): ByteArray {
        var bitmap = bitmap
        val height = bitmap.height
        val width = bitmap.width
        var bitWidth = (width + 7) / 8 * 8
        var scaleHeight = bitWidth / width * height
        bitmap = reSize(bitmap, bitWidth, scaleHeight)
        scaleHeight = bitmap.height
        bitWidth = bitmap.width
        println(scaleHeight)
        println(bitWidth)
        val data = IntArray(bitWidth * scaleHeight)
        var index = 0
        for (h in 0 until scaleHeight) {
            for (w in 0 until bitWidth) {
                data[index] = bitmap.getPixel(w, h)
                ++index
            }
        }
        var dataVec = ByteArray(bitWidth * scaleHeight)
        format_K_dither16x16(data, bitWidth, scaleHeight, dataVec)
        dataVec = pixToEscRastBitImageCmd(dataVec, bitWidth, scaleHeight)
        return dataVec
    }

    fun pixToEscRastBitImageCmd(src: ByteArray, nWidth: Int, height: Int): ByteArray {
        val data = ByteArray(8 + src.size / 8)
        data[0] = 29
        data[1] = 118
        data[2] = 48
        data[3] = 0
        data[4] = (nWidth / 8 % 256).toByte()
        data[5] = (nWidth / 8 / 256).toByte()
        data[6] = (height % 256).toByte()
        data[7] = (height / 256).toByte()
        var i = 8
        var k = 0
        while (i < data.size) {
            data[i] = (p0[src[k].toInt()] + p1[src[k + 1]
                .toInt()] + p2[src[k + 2].toInt()] + p3[src[k + 3]
                .toInt()] + p4[src[k + 4].toInt()] + p5[src[k + 5]
                .toInt()] + p6[src[k + 6].toInt()] + src[k + 7]).toByte()
            k += 8
            ++i
        }
        return data
    }

    private fun format_K_dither16x16_int(
        orgpixels: IntArray,
        xsize: Int,
        ysize: Int,
        despixels: IntArray
    ) {
        var k = 0
        for (y in 0 until ysize) {
            for (x in 0 until xsize) {
                if (orgpixels[k] and 255 > Floyd16x16[x and 15][y and 15]) {
                    despixels[k] = -1
                } else {
                    despixels[k] = -16777216
                }
                ++k
            }
        }
    }

    private fun format_K_dither16x16(
        orgpixels: IntArray,
        xsize: Int,
        ysize: Int,
        despixels: ByteArray
    ) {
        var k = 0
        for (y in 0 until ysize) {
            for (x in 0 until xsize) {
                if (orgpixels[k] and 255 > Floyd16x16[x and 15][y and 15]) {
                    despixels[k] = 0
                } else {
                    despixels[k] = 1
                }
                ++k
            }
        }
    }

    private fun changePointPx(arry: ByteArray): Int {
        var v = 0
        for (j in arry.indices) {
            if (arry[j].toInt() == 1) {
                v = v or (1 shl j)
            }
        }
        return v
    }

    fun reSize(bitmap: Bitmap, reWidth: Int, reHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val scaleWidth = reWidth.toFloat() / width.toFloat()
        val scaleHeight = reHeight.toFloat() / height.toFloat()
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
    }

    fun reSizeByWidth(bitmap: Bitmap, reWidth: Int): Bitmap {
        val width = bitmap.width
        val scaleWidth = reWidth.toFloat() / width.toFloat()
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleWidth)
        return Bitmap.createBitmap(bitmap, 0, 0, width, bitmap.height, matrix, true)
    }

    fun convertGreyImgByFloyd(img: Bitmap): Bitmap {
        val width = img.width
        val height = img.height
        val pixels = IntArray(width * height)
        img.getPixels(pixels, 0, width, 0, 0, width, height)
        val gray = IntArray(height * width)
        var e: Int
        var i: Int
        var j: Int
        var g: Int
        e = 0
        while (e < height) {
            i = 0
            while (i < width) {
                j = pixels[width * e + i]
                g = j and 16711680 shr 16
                gray[width * e + i] = g
                ++i
            }
            ++e
        }
        i = 0
        while (i < height) {
            j = 0
            while (j < width) {
                g = gray[width * i + j]
                if (g >= 128) {
                    pixels[width * i + j] = -1
                    e = g - 255
                } else {
                    pixels[width * i + j] = -16777216
                    e = g - 0
                }
                if (j < width - 1 && i < height - 1) {
                    gray[width * i + j + 1] += 3 * e / 8
                    gray[width * (i + 1) + j] += 3 * e / 8
                    gray[width * (i + 1) + j + 1] += e / 4
                } else if (j == width - 1 && i < height - 1) {
                    gray[width * (i + 1) + j] += 3 * e / 8
                } else if (j < width - 1 && i == height - 1) {
                    gray[width * i + j + 1] += e / 4
                }
                ++j
            }
            ++i
        }
        val mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        mBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return mBitmap
    }

    fun toGrays(bitmap: Bitmap): Bitmap {
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0.0f)
        val colorMatrixColorFilter = ColorMatrixColorFilter(colorMatrix)
        val gray = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.RGB_565)
        val canvas = Canvas(gray)
        val paint = Paint(1)
        paint.style = Paint.Style.STROKE
        paint.colorFilter = colorMatrixColorFilter
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint)
        return gray
    }
}