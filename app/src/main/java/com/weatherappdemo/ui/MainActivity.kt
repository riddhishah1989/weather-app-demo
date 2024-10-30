package com.weatherappdemo.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.weatherappdemo.R
import com.weatherappdemo.ui.dashboard.DashboardActivity
import com.weatherappdemo.viewmodel.SplashViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: SplashViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        viewModel = ViewModelProvider.create(this)[SplashViewModel::class.java]
        viewModel.navigateToLogin.observe(this) {
            if (it) {
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            }
        }
        viewModel.startSplashTimer()
    }
}