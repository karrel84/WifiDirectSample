package karrel.kr.co.wifidirectsample.util

import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.DecimalFormat

/**
 * Created by Rell on 2018. 11. 7..
 */
fun getStringSizeLengthFile(size: Long): String {

    val df = DecimalFormat("0.00")

    val sizeKb = 1024.0f
    val sizeMo = sizeKb * sizeKb
    val sizeGo = sizeMo * sizeKb
    val sizeTerra = sizeGo * sizeKb


    return when {
        size < sizeMo -> df.format(size / sizeKb) + " Kb"
        size < sizeGo -> df.format(size / sizeMo) + " Mb"
        size < sizeTerra -> df.format(size / sizeGo) + " Gb"
        else -> ""
    }

}

fun copyFile(inputStream: InputStream, out: OutputStream): Boolean {
    val buf = ByteArray(1024)
    var len: Int

    try {
        var len = inputStream.read(buf)
        while (len != -1) {
            out.write(buf, 0, len)
            len = inputStream.read(buf)
            println("copyFile len : $len")
        }
        out.close()
        inputStream.close()
    } catch (e: IOException) {
        return false
    }

    return true
}