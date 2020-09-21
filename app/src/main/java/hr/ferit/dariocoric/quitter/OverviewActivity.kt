package hr.ferit.dariocoric.quitter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_overview.*

class OverviewActivity : AppCompatActivity() {
    private var isNewUser: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)

        val preferences = getSharedPreferences("USER_DATA_PREFS", MODE_PRIVATE)
        val savedUid = preferences.getString("UID", null)
        val savedUN = preferences.getString("USERNAME", null)
        val savedDT = preferences.getString("DATETIME", null)
        val savedTS = preferences.getLong("TIMESTAMP", 0)

        Log.d("OverviewActivity", "Definitely sared prefs: $savedUid and $savedUN")
        Log.d("OverviewActivity", "Nigga quit on: $savedDT aka $savedTS")
        verifyUserIsLoggedIn()
        Log.d("OverviewActivity", "User is logged in")
       // checkForNewUser()

        Log.d("OverviewActivity", "Local variable: " + isNewUser.toString())


        Log.d("OverviewActivity", "Sucessfully verified this nigga")

        val overviewFragment = OverviewFragment()
        val healthFragment = HealthFragment()

        val btnOverviewFragment: Button = btn_overview_1
        val btnHealthFragment: Button = btn_overview_2

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, overviewFragment)
            addToBackStack(null)
            commit()
        }

        btnOverviewFragment.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, overviewFragment)
                addToBackStack(null)
                commit()
            }
        }

        btnHealthFragment.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, healthFragment)
                addToBackStack(null)
                commit()
            }
        }

    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        Log.d("Overview", "User logged in with uid: $uid")
        if(uid == null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun checkForNewUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/${uid}")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newUser = snapshot.child("newUser").getValue(Boolean::class.java)!!
                Log.d("OverviewActivity", "Data from db: " + newUser.toString())

                if(newUser) {
                    ref.child("newUser").setValue(false)
                    isNewUser = true
                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val preference = getSharedPreferences("USER_DATA_PREFS", MODE_PRIVATE)
                preference.edit().clear().apply()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            R.id.menu_social -> {
                val intent = Intent(this, SocialActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}