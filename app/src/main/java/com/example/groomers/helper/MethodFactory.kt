package com.example.ehcf.Helper

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.*
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.groomers.helper.Toastic
import java.net.InetAddress
import java.net.NetworkInterface
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit


val  currency="$"
fun progrossDilog(context: Context) {

    var progressDialog: ProgressDialog? = null
    progressDialog = ProgressDialog(context)
    progressDialog!!.setMessage("Loading..")
    progressDialog!!.setTitle("Please Wait")
    progressDialog!!.isIndeterminate = false
    progressDialog!!.setCancelable(true)
    progressDialog.show()
}

fun changeDateFormat4(date: String): String {
    val inf = SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy")
    val out = SimpleDateFormat("dd-MM-yyyy")
    return out.format(inf.parse(date))
}

fun convertTo12Hour(Time: String): String? {
    var Time = Time
    if (Time.length == 5) {
        Time = "$Time:00"
    }
    val f1: DateFormat = SimpleDateFormat("hh:mm:ss") //11:00 pm
    var d: Date? = null
    try {
        d = f1.parse(Time)
    } catch (e: ParseException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    }
    val f2: DateFormat = SimpleDateFormat("hh:mm a")
    return f2.format(d)
}

@RequiresApi(Build.VERSION_CODES.M)


fun isOnline(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (connectivityManager != null) {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                }

                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                }

                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
    }
    return false
}

fun getFinYear(): Int {
    val FIRST_FISCAL_MONTH: Int = Calendar.MARCH
    val month: Int = Calendar.getInstance().get(Calendar.MONTH)
    val year: Int = Calendar.getInstance().get(Calendar.YEAR)
    return if (month >= FIRST_FISCAL_MONTH) year else year - 1
}

fun hideKeyboard(activity: Activity) {
    val imm: InputMethodManager =
        activity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = activity.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun changeDateFormat(date: String): String {
    val inf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
    val out = SimpleDateFormat("dd/MM/yyyy  hh:mm aa")
    return out.format(inf.parse(date))
}

fun changeDateFormat1(date: String): String {
    val inf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
    val out = SimpleDateFormat("dd-MM-yyyy")
    return out.format(inf.parse(date))
}

fun changeDateFormatNew(date: String): String {
    val inf = SimpleDateFormat("yyyy-MM-dd")
    val out = SimpleDateFormat("yy/MM/dd")
    return out.format(inf.parse(date))
}

fun changeDateFormat5(date: String): String {
    val inf = SimpleDateFormat("dd-MM-yyyy")
    val out = SimpleDateFormat("yyyy-MM-dd")
    return out.format(inf.parse(date))
}

@RequiresApi(Build.VERSION_CODES.O)
fun changeDateFormat6(input: String): String {
    return try {
        val zonedDateTime = ZonedDateTime.parse(input)
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")
        zonedDateTime.format(formatter)
    } catch (e: Exception) {
        e.printStackTrace()
        input // return original if formatting fails
    }
}

fun changeDateFormat2(date: String): String {
    val inf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
    val out = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    return out.format(inf.parse(date))
}

var currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
var formattedDate: Int = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date()).toInt()
val currentMonth: Int = SimpleDateFormat("MM", Locale.getDefault()).format(Date()).toInt()

fun getMonth(monthPosition: Int): String {
    val month = when (monthPosition) {
        0 -> "January"
        1 -> "February"
        2 -> "March"
        3 -> "April"
        4 -> "May"
        5 -> "June"
        6 -> "July"
        7 -> "August"
        8 -> "September"
        9 -> "October"
        10 -> "November"
        11 -> "December"
        else -> ""
    }
    return month
}

fun getIPAddress(useIPv4: Boolean): String {
    try {
        val interfaces: List<NetworkInterface> =
            Collections.list(NetworkInterface.getNetworkInterfaces())
        for (intf in interfaces) {
            val addrs: List<InetAddress> = Collections.list(intf.inetAddresses)
            for (addr in addrs) {
                if (!addr.isLoopbackAddress) {
                    val sAddr: String = addr.hostAddress
                    //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                    val isIPv4 = sAddr.indexOf(':') < 0
                    if (useIPv4) {
                        if (isIPv4) return sAddr
                    } else {
                        if (!isIPv4) {
                            val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                            return if (delim < 0) sAddr.toUpperCase() else sAddr.substring(
                                0,
                                delim
                            ).toUpperCase()
                        }
                    }
                }
            }
        }
    } catch (ignored: Exception) {
    } // for now eat exceptions
    return ""
}

@Throws(ParseException::class)
fun formatToYesterdayOrToday(date: String?): String? {
    val dateTime = SimpleDateFormat("EEE hh:mma MMM d, yyyy").parse(date)
    val calendar = Calendar.getInstance()
    calendar.time = dateTime
    val today = Calendar.getInstance()
    val yesterday = Calendar.getInstance()
    yesterday.add(Calendar.DATE, -1)
    val timeFormatter: DateFormat = SimpleDateFormat("hh:mma")
    return if (calendar[Calendar.YEAR] === today[Calendar.YEAR] && calendar[Calendar.DAY_OF_YEAR] === today[Calendar.DAY_OF_YEAR]) {
        "Today " + timeFormatter.format(dateTime)
    } else if (calendar[Calendar.YEAR] === yesterday[Calendar.YEAR] && calendar[Calendar.DAY_OF_YEAR] === yesterday[Calendar.DAY_OF_YEAR]) {
        "Yesterday " + timeFormatter.format(dateTime)
    } else {
        date
    }
}


val toneG = ToneGenerator(AudioManager.STREAM_ALARM, 100)
fun beep(duration: Int) {
    toneG.startTone(ToneGenerator.TONE_DTMF_S, duration)
    val handler = Handler(Looper.getMainLooper())
    handler.postDelayed({
    }, (duration + 500).toLong())
}
@SuppressLint("WrongConstant")
fun myToast(context: Context, message: String, success: Boolean) {
    var int=1
    if (!success){
        int=3
    }
    Toastic.toastic(
        context = context,
        message =message,
        duration = Toastic.LENGTH_SHORT,
        type =int,
        isIconAnimated = true,
        textColor = if (false) Color.BLUE else null,
    ).show()
}
@RequiresApi(Build.VERSION_CODES.O)
fun covertTimeToText(dataDate: String): String {
    lateinit var convTime: String
    val prefix = ""
    val suffix = "Ago"
    try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val pasTime: Date = dateFormat.parse(dataDate)
        val nowTime = Date()
        val dateDiff: Long = nowTime.time - pasTime.time
        val second: Long = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
        val minute: Long = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
        val hour: Long = TimeUnit.MILLISECONDS.toHours(dateDiff)
        val day: Long = TimeUnit.MILLISECONDS.toDays(dateDiff)
        if (second < 60) {
            convTime = "$second Seconds $suffix"
        } else if (minute < 60) {
            convTime = "$minute Minutes $suffix"
        } else if (hour < 24) {
            convTime = "$hour Hours $suffix"
        } else if (day >= 7) {
            convTime = if (day > 360) {
                (day / 360).toString() + " Years " + suffix
            } else if (day > 30) {
                (day / 30).toString() + " Months " + suffix
            } else {
                (day / 7).toString() + " Week " + suffix
            }
        } else if (day < 7) {
            convTime = "$day Days $suffix"
        }
    } catch (e: ParseException) {
        e.printStackTrace()
        Log.e("ConvTimeE", e.toString())
    }
    return convTime


}
