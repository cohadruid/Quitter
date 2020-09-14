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
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class MainActivity : AppCompatActivity() {

    var dateFormat = SimpleDateFormat("dd.MM.yyyy.", Locale.GERMANY)
    var timeFormat = SimpleDateFormat("hh:mm", Locale.GERMANY)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        buttonTest.setOnClickListener {
            val now = Calendar.getInstance()
            val datePicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(Calendar.YEAR,year)
                selectedDate.set(Calendar.MONTH,month)
                selectedDate.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                val date = dateFormat.format(selectedDate.time)
                textViewDate.text = date.toString()
                Toast.makeText(this,"date : " + date,Toast.LENGTH_SHORT).show()
            },
                    now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))
            datePicker.show() }


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

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email/password", Toast.LENGTH_SHORT).show()
            return
        }

        //Log.d("MainActivity", "Email is $email")
        //Log.d("MainActivity", "Password is $password")


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

        val quitDate = textViewDate.text.toString()
        Log.d("Main", "Vrijeme: ${quitDate.substring(0,2)}:${quitDate.substring(3,4)}:${quitDate.substring(6,10)}")
        val quitUnixTime = (quitDate.substring(0,2).toLong()*24*3600 + (quitDate.substring(3,5).toLong()-1)*30*24*3600 + (quitDate.substring(6,10).toLong() - 1970)*365.25*24*3600).toLong()
        val user = User(uid, edit_text_username_register.text.toString(), quitDate, quitUnixTime)

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

class User(val uid: String, val user: String, val quitDate: String, val quitUnixTime: Long)
