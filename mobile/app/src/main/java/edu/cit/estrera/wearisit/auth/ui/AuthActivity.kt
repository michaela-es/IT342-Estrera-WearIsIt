package edu.cit.estrera.wearisit.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import edu.cit.estrera.wearisit.R
import edu.cit.estrera.wearisit.ui.viewmodel.AuthScreen
import edu.cit.estrera.wearisit.ui.viewmodel.AuthViewModel
import edu.cit.estrera.wearisit.ui.viewmodel.AuthViewModelFactory
import edu.cit.estrera.wearisit.data.local.TokenManager
import edu.cit.estrera.wearisit.data.remote.RetrofitClient
import edu.cit.estrera.wearisit.data.repository.AuthRepository

class AuthActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Initialize dependencies
        val tokenManager = TokenManager(this)
        val apiService = RetrofitClient.createApiService(tokenManager)
        val authRepository = AuthRepository(apiService, tokenManager)

        // Create ViewModel with factory
        val factory = AuthViewModelFactory(authRepository, tokenManager)
        viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        // Check for existing token
        viewModel.checkForToken()

        // Observe errors
        viewModel.error.observe(this) { error ->
            error?.let {
                android.widget.Toast.makeText(this, it, android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        // Observe success messages
        viewModel.successMessage.observe(this) { message ->
            message?.let {
                android.widget.Toast.makeText(this, it, android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        val switchLink: TextView = findViewById(R.id.tv_switch)

        // Observe screen changes
        viewModel.currentScreen.observe(this) { screen ->
            val fragment = when (screen) {
                AuthScreen.LOGIN -> LoginFragment()
                AuthScreen.REGISTER -> RegisterFragment()
                AuthScreen.PROFILE -> ProfileFragment()
            }

            supportFragmentManager.commit {
                replace(R.id.auth_container, fragment)
            }

            when (screen) {
                AuthScreen.LOGIN -> {
                    switchLink.text = "Don't have an account? Register"
                    switchLink.visibility = View.VISIBLE
                }
                AuthScreen.REGISTER -> {
                    switchLink.text = "Already have an account? Login"
                    switchLink.visibility = View.VISIBLE
                }
                AuthScreen.PROFILE -> {
                    switchLink.visibility = View.GONE
                }
            }
        }

        switchLink.setOnClickListener {
            when (viewModel.currentScreen.value) {
                AuthScreen.LOGIN -> viewModel.showRegister()
                AuthScreen.REGISTER -> viewModel.showLogin()
                else -> {}
            }
        }
    }
}