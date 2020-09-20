package hr.ferit.dariocoric.quitter

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Layout
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_health.*
import java.lang.Math.floor
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HealthFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HealthFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var preferences: SharedPreferences? = null
    private var quitTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        preferences = this.getActivity()!!
            .getSharedPreferences("USER_DATA_PREFS", AppCompatActivity.MODE_PRIVATE)
        quitTime = preferences!!.getLong("TIMESTAMP", 0) * 1000


        return inflater.inflate(R.layout.fragment_health, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<ListView>(R.id.listview_health)

        listView.adapter = ListViewAdapter(this.requireContext(), quitTime)
    }



    private class ListViewAdapter(context: Context, quitTS: Long): BaseAdapter() {

        private val mContext: Context
        val date = Calendar.getInstance()
        val dateNow: Long = date.timeInMillis
        val quitTime = quitTS
        private val healthHeaders = arrayListOf<String>(
            "20 minutes: Your blood pressure and heart rate decrease", "8 hours: The carbon monoxide level in your blood returns to normal"
        )
        private val healthHours = arrayListOf<Double>(0.33, 24.0)
        val hour = 60*60

        init {
            mContext = context
        }

        override fun getCount(): Int {
            //return 5
            return healthHeaders.size
        }

        override fun getItem(position: Int): Any {
            return "FILLER STRING"
        }

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val rowMain = layoutInflater.inflate(R.layout.health_fragment_row, viewGroup, false)
            val rowProgressBar = rowMain.findViewById<ProgressBar>(R.id.row_progressbar)
            val rowHeader = rowMain.findViewById<TextView>(R.id.row_title)
            val plusTime = healthHours.get(position) * hour * 1000
            val plusTime2 = plusTime / 1000

            val timeLeft = quitTime + plusTime - dateNow
            val timeLeft2 = timeLeft / 1000
            val timeDiff = (dateNow - quitTime)/1000
            //Log.d("HealthFragment", "dateNow: " + dateNow.toString())
            //Log.d("HealthFragment", "quitTime: " + quitTime.toString())
            val diffDays = floor(timeLeft / (24*60*60 * 1000))
            val diffHrs = floor((timeLeft/(60*60*1000))%60)
            val diffMins = (timeLeft / (60*1000)) % 60
            rowHeader.text = healthHeaders.get(position)
            //rowProgressBar.rotation = 180f
            rowProgressBar.progressTintList = ColorStateList.valueOf(Color.CYAN)
            rowProgressBar.progressBackgroundTintList = ColorStateList.valueOf(Color.BLUE)
            rowProgressBar.max = plusTime2.toInt()
            rowProgressBar.progress = timeDiff.toInt()
            //Log.d("HealthFragment", "PlusTime: " + plusTime2.toString())
            //Log.d("HealthFragment", "DiffCount: " + timeDiff.toString())
            val rowProgressText = rowMain.findViewById<TextView>(R.id.row_progress)
            when {
                diffMins < 0 -> {
                    rowProgressText?.text =  mContext.getString(R.string.health_congratulations)
                }
                diffHrs <= 0 -> {
                    rowProgressText?.text = (mContext.getString(R.string.health_reached) + " "
                            + diffMins.toInt().toString() + " " + mContext.getString(R.string.time_minutes))
                }
                diffDays <= 0 -> {
                    rowProgressText?.text = (mContext.getString(R.string.health_reached) + " "
                            + diffHrs.toInt().toString() + " " + mContext.getString(R.string.time_hours) + " "
                            + diffMins.toInt().toString() + " " + mContext.getString(R.string.time_minutes))
                }
                else -> {
                    rowProgressText?.text = (mContext.getString(R.string.health_reached) + " "
                            + diffDays.toInt().toString() + " " + mContext.getString(R.string.time_days) + " "
                            + diffHrs.toInt().toString() + " " + mContext.getString(R.string.time_hours) + " "
                            + diffMins.toInt().toString() + " " + mContext.getString(R.string.time_minutes))
                }
            }
            return rowMain
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
    }
}