package hr.ferit.dariocoric.quitter

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        text_view_dont_have_account.setOnClickListener {
           /* val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)*/
            finish()
        }
    }
}
