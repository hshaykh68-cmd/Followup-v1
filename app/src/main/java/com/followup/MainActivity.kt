package com.followup

import android.Manifest
import android.animation.ObjectAnimator
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.followup.domain.repository.SettingsRepository
import com.followup.presentation.navigation.AppNavigation
import com.followup.presentation.splash.AnimatedSplashContent
import com.followup.service.notification.NotificationPermissionHelper
import com.followup.presentation.theme.FollowUpTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var notificationPermissionHelper: NotificationPermissionHelper

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Permission result handled - app can proceed with or without notification permission
    }

    private val requestExactAlarmLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // Exact alarm permission result handled
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen BEFORE super.onCreate() for Android 12+ API
        val splashScreen = installSplashScreen()
        
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Keep splash visible during initialization
        var isSplashVisible by mutableStateOf(true)
        
        splashScreen.setKeepOnScreenCondition { isSplashVisible }

        // Setup exit animation for Android 12+ (API 31+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreen.setOnExitAnimationListener { splashScreenView ->
                // Premium slide-up exit with anticipation
                val slideUp = ObjectAnimator.ofFloat(
                    splashScreenView.view,
                    View.TRANSLATION_Y,
                    0f,
                    -splashScreenView.view.height.toFloat() * 0.3f
                )
                
                slideUp.apply {
                    duration = 250
                    interpolator = AnticipateInterpolator(1.2f)
                    doOnEnd {
                        splashScreenView.remove()
                    }
                    start()
                }
            }
        }

        checkAndRequestPermissions()

        setContent {
            val darkModeEnabled by settingsRepository.isDarkModeEnabled()
                .collectAsState(initial = false)
            
            // Track splash animation completion
            var showMainContent by remember { mutableStateOf(false) }

            FollowUpTheme(darkTheme = darkModeEnabled) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (!showMainContent) {
                        // Premium animated splash (0-1000ms)
                        AnimatedSplashContent(
                            onAnimationComplete = {
                                // Seamless handoff: hide system splash then show main
                                isSplashVisible = false
                                showMainContent = true
                            }
                        )
                    } else {
                        // Main app content with fade-in
                        AppNavigation(
                            settingsRepository = settingsRepository,
                            notificationPermissionHelper = notificationPermissionHelper
                        )
                    }
                }
            }
        }
    }

    private fun checkAndRequestPermissions() {
        // Request POST_NOTIFICATIONS permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Show rationale if needed, then request
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }

        // Check SCHEDULE_EXACT_ALARM permission for Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(ALARM_SERVICE) as android.app.AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = android.content.Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                requestExactAlarmLauncher.launch(intent)
            }
        }
    }
}
