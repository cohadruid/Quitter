package hr.ferit.dariocoric.quitter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.fragment_overview.*
import java.math.RoundingMode
import java.sql.Date
import java.sql.Timestamp
import java.text.DecimalFormat
import java.util.*
import kotlin.math.round

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OverviewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OverviewFragment : Fragment() {
    var localTS: Long = 0
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val decFormat = DecimalFormat("#.##")
        decFormat.roundingMode = RoundingMode.CEILING

        val preferences = this.getActivity()?.getSharedPreferences("USER_DATA_PREFS", AppCompatActivity.MODE_PRIVATE)
        val savedUid = preferences?.getString("UID", null)
        val savedUN = preferences?.getString("USERNAME", null)
        val savedDT = preferences?.getString("DATETIME", null)
        val savedTS = preferences?.getLong("TIMESTAMP", 0)
        val cpd = preferences?.getInt("CIGS_PER_DAY", 0)
        val cpp = preferences?.getInt("CIGS_PER_PACK", 0)
        val ppp = preferences?.getInt("PRICE_PER_PACK", 0)

        val tvTime: TextView = view!!.findViewById(R.id.tv_time)
        val tvMoney: TextView = view!!.findViewById(R.id.tv_money_saved)

        val day = 24*60*60

        val rightNow: Calendar = Calendar.getInstance()

        val rnLong: Long = rightNow.timeInMillis.toLong()

        Log.d("OverviewFragment", "Milis: " + rnLong.toString())

        var  timeElapsed = rnLong/1000 - savedTS!!

        val daysPassed = timeElapsed / (3600*24)
        val hrsPassed = (timeElapsed / 3600) % 24
        val minsPassed = (timeElapsed / 60) % 60
        val packsPerDay: Float = cpd!!.toFloat() / cpp!!
        val money_saved_ratio: Float = (timeElapsed.toFloat() / day) * packsPerDay
        val cns_ratio: Float = timeElapsed.toFloat() / day
        val moneySaved = decFormat.format((ppp!!.times(money_saved_ratio)))
        val cigsNotSmoked = decFormat.format((cpd!!.times(cns_ratio)))
        tvTime.text = "It's been $daysPassed days $hrsPassed hours $minsPassed minutes"
        tvMoney.text = "You saved HRK $moneySaved"
        tv_cigs_not_smoked.text = "You avoided $cigsNotSmoked cigarettes!"
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (getFragmentManager() != null) {
                getFragmentManager()
                    ?.beginTransaction()
                    ?.detach(this)
                    ?.attach(this)
                    ?.commit()
            }
        }
    }
}

