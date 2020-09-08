package hr.ferit.dariocoric.quitter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
            }
            .addOnFailureListener {
                Log.d("MainActivity", "Failed to create user: ${it.message}")
                Toast.makeText(this, "Please enter credentials", Toast.LENGTH_SHORT).show()
            }
    }
}
