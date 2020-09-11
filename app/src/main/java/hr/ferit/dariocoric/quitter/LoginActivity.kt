package hr.ferit.dariocoric.quitter

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*

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
                    val intent = Intent(this, OverviewActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
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
}
