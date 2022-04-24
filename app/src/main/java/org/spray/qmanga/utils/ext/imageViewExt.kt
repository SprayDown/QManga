package org.spray.qmanga.utils.ext

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat


fun ImageView.setAnimVectorCompat(@DrawableRes drawable: Int, @AttrRes tint: Int? = null) {
    val vector = AnimatedVectorDrawableCompat.create(context, drawable)
    if (tint != null) {
        vector?.mutate()
        vector?.setTint(context.themeColor(tint))
    }
    setImageDrawable(vector)
    vector?.start()
}

fun ImageView.setVectorCompat(@DrawableRes drawable: Int, @AttrRes tint: Int? = null) {
    val vector = AppCompatResources.getDrawable(context, drawable)
    if (tint != null) {
        vector?.mutate()
        vector?.setTint(context.themeColor(tint))
    }
    setImageDrawable(vector)
}

fun Bitmap.blurRenderScript(context: Context?, radius: Int): Bitmap? {
    var smallBitmap = this
    try {
        smallBitmap = RGB565toARGB888(smallBitmap)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    val bitmap = Bitmap.createBitmap(
        smallBitmap.width, smallBitmap.height,
        Bitmap.Config.ARGB_8888
    )
    val renderScript = RenderScript.create(context)
    val blurInput = Allocation.createFromBitmap(renderScript, smallBitmap)
    val blurOutput = Allocation.createFromBitmap(renderScript, bitmap)
    val blur = ScriptIntrinsicBlur.create(
        renderScript,
        Element.U8_4(renderScript)
    )
    blur.setInput(blurInput)
    blur.setRadius(radius.toFloat()) // radius must be 0 < r <= 25
    blur.forEach(blurOutput)
    blurOutput.copyTo(bitmap)
    renderScript.destroy()
    return bitmap
}

@Throws(Exception::class)
fun RGB565toARGB888(img: Bitmap): Bitmap {
    val numPixels = img.width * img.height
    val pixels = IntArray(numPixels)

    //Get JPEG pixels.  Each int is the color values for one pixel.
    img.getPixels(pixels, 0, img.width, 0, 0, img.width, img.height)

    //Create a Bitmap of the appropriate format.
    val result = Bitmap.createBitmap(img.width, img.height, Bitmap.Config.ARGB_8888)

    //Set RGB pixels.
    result.setPixels(pixels, 0, result.width, 0, 0, result.width, result.height)
    return result
}