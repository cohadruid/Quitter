package hr.ferit.dariocoric.quitter

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import jhonatan.sabadi.datetimepicker.showDateAndTimePicker
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_overview.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {

   var dateFormat = SimpleDateFormat("dd.MM.yyyy. HH:mm", Locale.GERMANY)
   // var timeFormat = SimpleDateFormat("hh:mm", Locale.GERMANY)
    private var dateSecs: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edittext_quittime.setOnClickListener {
            showDateAndTimePicker { year: Int, month: Int, dayOfMonth: Int, hourOfDay: Int, minute: Int ->
                val selectedDateTime = Calendar.getInstance()
                selectedDateTime.set(Calendar.YEAR, year)
                selectedDateTime.set(Calendar.MONTH, month)
                selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedDateTime.set(Calendar.MINUTE, minute)

                dateSecs = selectedDateTime.timeInMillis / 1000

                edittext_quittime.setText(dateFormat.format(selectedDateTime.time).toString())

            }
        }


        button_register_main.setOnClickListener {
            performRegister()
        }

        text_view_login_main.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performRegister() {
        val email = edit_text_email_register.text.toString()
        val password = edit_text_password_register.text.toString()
        val quitdate = edittext_quittime.text.toString()

        if(email.isEmpty() || password.isEmpty() || quitdate.isEmpty()) {
            Toast.makeText(this, "Please enter email/password", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("MainActivity", "Email is $email")
        Log.d("MainActivity", "Password is $password")


        //creating user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(!it.isSuccessful) return@addOnCompleteListener

                Log.d("MainActivity", "Created user with uid ${it.result?.user?.uid}")
                saveUserToFirebaseDatabase();
            }
            .addOnFailureListener {
                Log.d("MainActivity", "Failed to create user: ${it.message}")
                Toast.makeText(this, "Please enter credentials", Toast.LENGTH_SHORT).show()
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveUserToFirebaseDatabase() {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val preferences = getSharedPreferences("USER_DATA_PREFS", MODE_PRIVATE)
        val editor = preferences.edit()
        editor.apply {
            putString("UID", uid)
            putString("USERNAME", edit_text_username_register.text.toString())
            putLong("TIMESTAMP", dateSecs)
            putString("DATETIME", edittext_quittime.text.toString())
            putInt("CIGS_PER_DAY", edittext_cigs_per_day.text.toString().toInt())
            putInt("CIGS_PER_PACK", edittext_cigs_per_pack.text.toString().toInt())
            putInt("PRICE_PER_PACK", edittext_price_per_pack.text.toString().toInt())
        }.apply()


        val user = User(uid, edit_text_username_register.text.toString(),dateSecs,  edittext_quittime.text.toString(), edittext_cigs_per_day.text.toString().toInt(), edittext_cigs_per_pack.text.toString().toInt(), edittext_price_per_pack.text.toString().toInt())

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("Main", "User data saved successfully")
                val intent = Intent(this, OverviewActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("Main", "Failed to set value to database: ${it.message}")
            }
    }
}

class User(val uid: String, val user: String, val quitTimeStamp: Long, val datetime: String, val cpd: Int, val cpp: Int, val ppp: Int)
