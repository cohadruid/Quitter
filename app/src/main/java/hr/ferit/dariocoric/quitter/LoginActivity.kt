package hr.ferit.dariocoric.quitter

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Thread.sleep

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        button_login_login.setOnClickListener {
            val email = edit_text_email_login.text.toString()
            val password = edit_text_password_login.text.toString()

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if(!it.isSuccessful) {
                        Log.d("LoginActivity","Attempted to login with e: $email and p: $password")
                        Toast.makeText(this, "Check email/password", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    }

                    Log.d("LoginActivity","Attempted to login with e: $email and p: $password")
                    Log.d("LoginActivity", "UID: ${it.result?.user?.uid}")
                    getUserData()
                }
                .addOnFailureListener {
                    Log.d("LoginActivity", "Failed to auth user - ${it.message}")
                }
        }

        text_view_dont_have_account.setOnClickListener {
           /* val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)*/
            finish()
        }
    }

    private fun getUserData() {
        val uid = FirebaseAuth.getInstance().uid
        val preferences = getSharedPreferences("USER_DATA_PREFS", MODE_PRIVATE)
        val editor = preferences.edit()
        val ref = FirebaseDatabase.getInstance().getReference("/users/${uid}")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val uUid = uid
                val username = snapshot.child("user").getValue(String::class.java)!!
                val quitTS = snapshot.child("quitTimeStamp").getValue(Long::class.java)!!
                val quitDateTime = snapshot.child("datetime").getValue(String::class.java)!!
                val cpd = snapshot.child("cpd").getValue(Int::class.java)!!
                val cpp = snapshot.child("cpp").getValue(Int::class.java)!!
                val ppp = snapshot.child("ppp").getValue(Int::class.java)!!
                editor.apply {
                    putString("UID", uid)
                    putString("USERNAME", username)
                    putLong("TIMESTAMP", quitTS)
                    putString("DATETIME", quitDateTime)
                    putInt("CIGS_PER_DAY", cpd)
                    putInt("CIGS_PER_PACK", cpp)
                    putInt("PRICE_PER_PACK", ppp)
                }.apply()
                val intent = Intent(this@LoginActivity, OverviewActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}
