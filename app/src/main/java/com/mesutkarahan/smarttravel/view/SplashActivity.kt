package com.mesutkarahan.smarttravel.view
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.mesutkarahan.smarttravel.R
import com.mesutkarahan.smarttravel.databinding.ActivitySplashBinding
import com.mesutkarahan.smarttravel.databinding.FragmentMapBinding

class SplashActivity : AppCompatActivity() {

    private var _binding: ActivitySplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val scaleUpAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_up)

        binding.splashLogo.startAnimation(fadeInAnimation)
        binding.splashText.startAnimation(scaleUpAnimation)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
