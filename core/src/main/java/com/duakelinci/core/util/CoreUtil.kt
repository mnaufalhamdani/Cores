@file:Suppress("DEPRECATION")

package com.duakelinci.core.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ClipData
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.telephony.TelephonyManager
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.format.DateFormat
import android.util.Base64
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.destination
import id.zelory.compressor.constraint.quality
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

object CoreUtil {

    /**
     * FUNCTION TO DELETE FOLDER OR FILE
     * @param file
     *
     */
    @Throws(Exception::class)
    fun delete(file: File) {
        if (file.isDirectory) {
            file.listFiles()?.forEach {
                delete(it)
            }
        } else if (file.isFile) {
            Timber.d("is File and ${file.absolutePath}")
            file.delete()
        }
    }

    @Throws(Exception::class)
    fun delete2(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) {
            fileOrDirectory.listFiles()?.forEach {
                delete2(it)
            }
        }

        if (fileOrDirectory.exists()) {
            val b = fileOrDirectory.delete()
            if (b) {
                Timber.d("FileUtil: delete success")
            } else {
                Timber.e("FileUtil: delete failed")
            }
        }
    }

    fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = context.contentResolver.query(contentUri, proj, null, null, null)
            val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor?.moveToFirst()
            val result = columnIndex?.let { cursor.getString(it) } ?: contentUri.path
            cursor?.close()
            return result
        } catch (e: Exception) {
            Timber.e(e)
        }
        return contentUri.path
    }

    suspend fun compressFile(
        path: String,
        context: Context,
        imageFile: File,
        compressQuality: Int = 80,
        isAddToGallery: Boolean = false
    ): File? {
        val folder =
            File(Environment.getExternalStorageDirectory().toString() + "/$path")
        var success = true
        if (!folder.exists()) {
            success = folder.mkdirs()
        }
        if (success) {
            if (!isAddToGallery){
                val file = File("${folder.path}/.nomedia")
                Timber.d("${folder.path}/.nomedia $file")
                try {
                    file.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            try {
                val imageCompress = Compressor.compress(context, imageFile) {
                    quality(compressQuality)
                    destination(folder.absoluteFile)
                }

                if (isAddToGallery) galleryAddPic(context, imageCompress)
                return imageCompress
            } catch (e: Exception) {
                Timber.e(e)
            }
        } else {
            Timber.e("FileUtil: Cannot Create Folder")
        }
        return null
    }

    @Throws(IOException::class)
    fun createImageFile(filename:String, suffix:String, directory:File): File {
        // Create an image file name
        return File.createTempFile(
                filename, /* prefix */
                suffix, /* suffix */
                directory /* directory */
        )
    }

    @Throws(IOException::class)
    fun galleryAddPic(context: Context, file: File?) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        file?.let {
            val contentUri = Uri.fromFile(file)
            mediaScanIntent.data = contentUri
        }
        context.sendBroadcast(mediaScanIntent)
    }

    fun convertStringToUri(context: Context, stringImage: String, title: String = "Title"): Uri {
        val bytes = ByteArrayOutputStream()
        val bitmap = convertStringToBitmap(stringImage)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            context.contentResolver,
            bitmap,
            title,
            null
        )
        return Uri.parse(path)
    }

    fun convertStringToBitmap(imageString: String): Bitmap {
        val decodedString = Base64.decode(imageString, Base64.NO_WRAP)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    fun convertFileToBitmap(fileImage: File): Bitmap {
        return imgFileToBitmap(fileImage)
    }

    fun convertFileToBitmap(context: Context, fileImage: File): Bitmap {
        return imgFileToBitmap(context, fileImage)
    }

    fun convertImgToString(fileImage: File, compressQuality: Int = 100): String {
        val bitmap = imgFileToBitmap(fileImage)
        return bitmapToString(bitmap, compressQuality)
    }

    fun convertImgToString(context: Context, fileImage: File, compressQuality: Int = 100): String {
        val bitmap = imgFileToBitmap(context, fileImage)
        return bitmapToString(bitmap, compressQuality)
    }

    fun convertImgToString(bitmap: Bitmap, compressQuality: Int = 100): String {
        return bitmapToString(bitmap, compressQuality)
    }

    private fun bitmapToString(bitmap: Bitmap, compressQuality: Int): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, outputStream)
        val byte = outputStream.toByteArray()
        return Base64.encodeToString(byte, Base64.NO_WRAP)
    }

    private fun imgFileToBitmap(fileImage: File): Bitmap {
        val filePath = fileImage.path
        return BitmapFactory.decodeFile(filePath)
    }

    private fun imgFileToBitmap(context: Context, fileImage: File): Bitmap {
        return MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.fromFile(fileImage))
    }

    fun dpToPx(context: Context, dp: Int): Int {
        val resource = context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resource.displayMetrics
        ).roundToInt()
    }

    fun drawMultilineTextToBitmap(
        context: Context,
        bitmap: Bitmap,
        text: String,
        colorText: Int
    ): Bitmap {
        val scale = context.resources.displayMetrics.density

        val bitmapConfig = bitmap.config

        val mBitmap = bitmap.copy(bitmapConfig, true)

        val canvas = Canvas(mBitmap)
        // new antialiased Paint
        val paint = TextPaint(Paint(Paint.ANTI_ALIAS_FLAG))
        // text color - #3D3D3D
        paint.color = colorText
        // text size in pixels
        paint.textSize = 12 * scale
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE)

        // set text width to canvas width minus 16dp padding
        val textWidth = (canvas.width - 16 * scale)
        val textLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val sb = StaticLayout.Builder.obtain(text, 0, text.length, paint, textWidth.toInt())
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setLineSpacing(0.0f, 1.0f)
                .setIncludePad(false)
            sb.build()
        } else {
            StaticLayout(
                text,
                paint,
                textWidth.toInt(),
                Layout.Alignment.ALIGN_CENTER,
                1.0f,
                0.0f,
                false
            )
        }
        /*val textLayout =
            StaticLayout(
                text,
                paint,
                textWidth.toInt(),
                Layout.Alignment.ALIGN_CENTER,
                1.0f,
                0.0f,
                false
            )*/
        val textHeight = textLayout.height
        val x = (mBitmap.width - textWidth) / 2
        val y = (mBitmap.height - textHeight) * 8 / 9
        canvas.save()
        canvas.translate(x, y.toFloat())
        textLayout.draw(canvas)
        canvas.restore()
        return mBitmap
    }

    private fun printHexBinary(data: ByteArray): String {
        val hexChars = "0123456789ABCDEF".toCharArray()
        val r = StringBuilder(data.size * 2)
        data.forEach { b ->
            val i = b.toInt()
            r.append(hexChars[i shr 4 and 0xF])
            r.append(hexChars[i and 0xF])
        }
        return r.toString()
    }

    fun setCircleImageToImageView(
        context: Context?,
        imageView: ImageView,
        drawable: Int,
        borderWidth: Int,
        color: Int
    ) {
        val requestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
            .skipMemoryCache(true)
            .circleCrop()

        if (borderWidth > 0) {
            Glide.with(context!!)
                .load(drawable)
                .apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: com.bumptech.glide.request.transition.Transition<in Drawable>?
                    ) {

                        imageView.setImageDrawable(resource)

                        try {
                            val colorContextCompat = ContextCompat.getColor(context, color)
                            val bitmap = (resource as BitmapDrawable).bitmap

                            if (bitmap != null) {

                                val d = BitmapDrawable(
                                    context.resources, getCircularBitmapWithBorder(
                                        bitmap,
                                        borderWidth,
                                        colorContextCompat
                                    )
                                )

                                imageView.setImageDrawable(d)
                            } else {
                                imageView.setImageDrawable(resource)
                            }
                        } catch (e: Exception) {
                            Timber.e(e)
                        }

                    }
                })
        } else {
            Glide.with(context!!)
                .load(drawable)
                .apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView)
        }
    }

    fun getCircularBitmapWithBorder(bitmap: Bitmap?, borderWidth: Int, color: Int): Bitmap? {
        if (bitmap == null || bitmap.isRecycled) {
            return null
        }

        val width = bitmap.width + borderWidth
        val height = bitmap.height + borderWidth

        val canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = shader

        val canvas = Canvas(canvasBitmap)
        val radius = if (width > height) height.toFloat() / 2f else width.toFloat() / 2f
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius, paint)
        paint.shader = null
        paint.style = Paint.Style.STROKE
        paint.color = color
        paint.strokeWidth = borderWidth.toFloat()
        canvas.drawCircle(
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            radius - borderWidth / 2,
            paint
        )
        return canvasBitmap
    }

    fun fromMinutesToHHmm(minutes: Int): String {
        val hours = TimeUnit.MINUTES.toHours(minutes.toLong())
        val remainMinutes = minutes - TimeUnit.HOURS.toMinutes(hours)
        var text = ""
        if (hours > 0) text = "$hours Jam "
        if (remainMinutes > 0) text = "$text$remainMinutes Menit"
        return text
    }

    fun getSettingTime(contentResolver: ContentResolver): String {
        return android.provider.Settings.System.getString(
            contentResolver,
            android.provider.Settings.Global.AUTO_TIME
        )
    }

    fun getSettingTimeZone(contentResolver: ContentResolver): String {
        return android.provider.Settings.System.getString(
            contentResolver,
            android.provider.Settings.Global.AUTO_TIME_ZONE
        )
    }

    fun isSimReady(context: Context): Boolean{
        val telMgr = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return when (telMgr.simState) {
            TelephonyManager.SIM_STATE_READY -> {
                true
            }
            else -> false
        }
    }

    fun getCurrencyFormat(value: Int) : String {
        return try {
            val formatter  = NumberFormat.getCurrencyInstance(Locale.getDefault())
            formatter.minimumFractionDigits = 0
            val decimalFormatSymbols: DecimalFormatSymbols = (formatter as DecimalFormat).decimalFormatSymbols
            decimalFormatSymbols.monetaryDecimalSeparator = ','
            decimalFormatSymbols.decimalSeparator = ','
            decimalFormatSymbols.groupingSeparator = '.'
            decimalFormatSymbols.currencySymbol = ""
            formatter.decimalFormatSymbols = decimalFormatSymbols
            return formatter.format(value)
        } catch (nfe: NumberFormatException) {
            nfe.printStackTrace()
            value.toString()
        }
    }

    fun getCurrencyFormat(value: String) : String {
        try {
            var originalString = value.replace(".", ",")

            val longval: Long?
            if (originalString.contains(",")) {
                originalString = originalString.replace(",".toRegex(), "")
            }
            longval = originalString.toLong()

            val formatter  = NumberFormat.getInstance(Locale.US) as DecimalFormat
            formatter.applyPattern("#,###,###,###")
            val formattedString = formatter.format(longval)
            return formattedString.replace(",", ".")
        } catch (nfe: NumberFormatException) {
            nfe.printStackTrace()
            return value
        }
    }

    private fun customFormatText(value: String, pFormat: String):String{
        try {
            val format = StringBuilder(pFormat)
            val fixString = StringBuilder(value)
            val tempString = StringBuilder()
            tempString.append(fixString.toString().replace(".", "").replace("-", ""))
            tempString.mapIndexed { index, c ->
                println("${tempString.length} $index $c")
                if (index < format.length-1 && !format[index + 1].isLetterOrDigit()){
                    tempString.insert(index + 1, format[index + 1])
                }
            }
            fixString.replace(0, fixString.length, tempString.toString())
            return fixString.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            return value
        }
    }

    fun formatSizeUnits(size: Int): String {
        val bytes: String
        val numberFormat= NumberFormat.getInstance()
        bytes = when {
            size >= 1073741824 -> {
                "${numberFormat.format((size / 1073741824)).toDouble()} GB"
            }
            size >= 1048576 -> {
                "${numberFormat.format((size / 1048576)).toDouble()} MB"
            }
            size >= 1024 -> {
                "${numberFormat.format((size / 1024))} KB"
            }
            size > 1 -> {
                "${numberFormat.format(size)} Bytes"
            }
            size == 1 -> {
                "${numberFormat.format(size)} Byte"
            }
            else -> {
                "0 bytes"
            }
        }
        return bytes
    }

    fun hideFirstFab(v: View) {
        v.visibility = View.GONE
        v.translationY = v.height.toFloat()
        v.alpha = 0f
    }

    fun hideFab(v: View) {
        v.visibility = View.VISIBLE
        v.alpha = 1f
        v.translationY = 0f
        v.animate()
            .setDuration(300)
            .translationY(v.height.toFloat())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    v.visibility = View.GONE
                    super.onAnimationEnd(animation)
                }
            })
            .alpha(0f)
            .start()
    }

    fun showFab(v: View) {
        v.visibility = View.VISIBLE
        v.alpha = 0f
        v.translationY = v.height.toFloat()
        v.animate()
            .setDuration(300)
            .translationY(0f)
            .setListener(object : AnimatorListenerAdapter() {})
            .alpha(1f)
            .start()
    }

    fun twistFab(v: View, rotate: Boolean) : Boolean {
        v.animate()
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {})
            .rotation(
                if (rotate) 165f
                else 0f
            )
        return rotate
    }

    fun terbilang(pTotal: Int) : String {
        val total= abs(pTotal)
        val angka= mutableListOf(
            "",
            "Satu ",
            "Dua ",
            "Tiga ",
            "Empat ",
            "Lima ",
            "Enam ",
            "Tujuh ",
            "Delapan ",
            "Sembilan ",
            "Sepuluh ",
            "Sebelas "
        )
        var temp = ""

        when {
            total < 12 -> {
                temp = " "+ angka[total]
            }
            total < 20 -> {
                temp = terbilang(total.minus(10)) +" Belas"
            }
            total < 100 -> {
                temp = terbilang(total.div(10)) +" Puluh "+ terbilang(total % 10)
            }
            total < 200 -> {
                temp = "Seratus"+ terbilang(total.minus(100))
            }
            total < 1000 -> {
                temp = terbilang(total.div(100)) +" Ratus "+ terbilang(total % 100)
            }
            total < 2000 -> {
                temp = "Seribu"+ terbilang(total.minus(1000))
            }
            total < 1000000 -> {
                temp = terbilang(total.div(1000)) +" Ribu "+ terbilang(total % 1000)
            }
            total < 1000000000 -> {
                temp = terbilang(total.div(1000000)) +" Juta "+ terbilang(total % 1000000)
            }
            total < 1000000000000 -> {
                temp = terbilang(total.div(1000000000)) +" Milyar "+ terbilang(total % 1000000000)
            }
        }
        return temp
    }

    /**
     * Function baru 07/02/2024
     */
    fun getBatteryPercentage(context: Context): Int {
        val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }

    fun getIpAddress(context: Context): String? {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.hostAddress
                    }
                }
            }
        } catch (ex: SocketException) {
            ex.printStackTrace()
            val mManager =
                context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val mInfo = mManager.connectionInfo.ipAddress
            return String.format(
                "%d.%d.%d.%d",
                mInfo and 0xff,
                mInfo shr 8 and 0xff,
                mInfo shr 16 and 0xff,
                mInfo shr 24 and 0xff
            )
        }
        return "Tidak Diketahui"
    }

    fun openDatePicker(
        context: Context,
        dateNow: Long? = null,
        dateMin: Long? = null,
        dateMax: Long? = null,
        listenerClickItem: (date: Long) -> Unit
    ) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = dateNow ?: System.currentTimeMillis()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val datePicker = DatePickerDialog(
            context,
            { _, _year, _month, dayOfMonth ->

                val calendar = Calendar.getInstance()
                calendar.set(Calendar.YEAR, _year)
                calendar.set(Calendar.MONTH, _month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                listenerClickItem.invoke(calendar.timeInMillis)
            }, year, month, day
        )
        dateMin?.let {
            datePicker.datePicker.minDate = it
        }
        dateMax?.let {
            datePicker.datePicker.maxDate = it
        }
        datePicker.show()
    }

    fun openTimePicker(context: Context, timeStamp: String? = null, listenerClickItem: (time: String) -> Unit) {
        val calendar = Calendar.getInstance()
        var iHour = calendar[Calendar.HOUR_OF_DAY]
        var iMinute = calendar[Calendar.MINUTE]

        if (!timeStamp.isNullOrEmpty()){
            val newTime = timeStamp.split(":")
            iHour = newTime[0].toInt()
            iMinute = newTime[1].toInt()
        }

        val timePickerDialog = TimePickerDialog(
            context, { _, hourOfDay, minute ->
                listenerClickItem.invoke(
                    "${String.format("%02d", hourOfDay)}:${String.format("%02d", minute)}"
                )
            }, iHour, iMinute, DateFormat.is24HourFormat(context)
        )
        timePickerDialog.show()
    }

    fun copyClipboard(context: Context, text: String) {
        val clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = ClipData.newPlainText("Tersalin", text)
        clipboard.setPrimaryClip(clip)
    }

    fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = (sin(deg2rad(lat1))
                * sin(deg2rad(lat2))
                + (cos(deg2rad(lat1))
                * cos(deg2rad(lat2))
                * cos(deg2rad(theta))))
        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515
        return milesToMeters(dist)
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

    private fun milesToMeters(miles: Double): Double {//1609.344 = meter in miles
        return miles * 1609.344
    }

    fun getVersionOS(): String {
        val release = java.lang.Double.parseDouble(
            java.lang.String(Build.VERSION.RELEASE).replaceAll("(\\d+[.]\\d+)(.*)", "$1")
        )
        var codeName = "Unsupported"//below Jelly Bean
        if (release >= 4.1 && release < 4.4) codeName = "Jelly Bean"
        else if (release < 5) codeName = "Kit Kat"
        else if (release < 6) codeName = "Lollipop"
        else if (release < 7) codeName = "Marshmallow"
        else if (release < 8) codeName = "Nougat"
        else if (release < 9) codeName = "Oreo"
        else if (release < 10) codeName = "Pie"
        else if (release < 11) codeName = "Android" + (release.toInt()) + " (Q)"
        else if (release < 12) codeName = "Android" + (release.toInt()) + " (R)"
        else if (release < 13) codeName = "Android" + (release.toInt()) + " (S)"
        else if (release < 14) codeName = "Tiramisu"
        return codeName + " v" + release + ", API Level: " + Build.VERSION.SDK_INT
    }

    fun secToTime(sec: Int): String {
        val seconds = sec % 60
        var minutes = sec / 60
        if (minutes >= 60) {
            val hours = minutes / 60
            minutes %= 60
            if (hours >= 24) {
                val days = hours / 24
                return String.format("%d hari %02d:%02d:%02d", days, hours % 24, minutes, seconds)
            }
            return String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }
        return String.format("00:%02d:%02d", minutes, seconds)
    }

    fun timeToSecond(time: String): Int {
        val times = time.split(":")//HH:mm:ss
        val hour = times[0].toInt()
        val minute = times[1].toInt()
        val second = times[2].toInt()

        val duration = (hour * 3600) + (minute * 60) + second
        return duration
    }

    fun secondToTime(sec: Int): String {
        val hours = sec / 3600
        val minutes = (sec % 3600) / 60
        val seconds = sec % 60

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    @SuppressLint("DefaultLocale")
    @Suppress("DEPRECATION")
    fun capitalizeEachWord(text: String): String {
        var str = text.lowercase()
        val space = " "
        val splitedStr = str.split(space)
        str = splitedStr.joinToString (space){
            it.capitalize()
        }
        return str
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateDiff(format: String, newDate: String, oldDate: String): Int {
        return try {
            val newFormat = SimpleDateFormat(format)
            TimeUnit.DAYS.convert(
                newFormat.parse(newDate).time - newFormat.parse(oldDate).time,
                TimeUnit.MILLISECONDS
            ).toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun convertDateLongToString(dateTime: Long, format: String? = "yyyy-MM-dd HH:mm:ss"): String {
        val formater = SimpleDateFormat(format)
        val date = Date(dateTime)
        return formater.format(date)
    }
}