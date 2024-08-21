package com.example.oriientsdktestapp

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.oriientsdktestapp.ui.theme.OriientSdkTestAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import me.oriient.ipssdk.api.listeners.IPSCompletionListener
import me.oriient.ipssdk.api.listeners.IPSLoginListener
import me.oriient.ipssdk.api.listeners.IPSLogoutListener
import me.oriient.ipssdk.api.models.IPSError
import me.oriient.ipssdk.api.models.IPSSpace
import me.oriient.ipssdk.ips.IPSCore
import me.oriient.ipssdk.ips.IPSPositioning

class MainActivity : ComponentActivity() {

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(ACCESS_FINE_LOCATION, false) -> {
                Toast.makeText(
                    applicationContext,
                    "Fine permission granted",
                    Toast.LENGTH_LONG
                ).show()
            }

            permissions.getOrDefault(ACCESS_COARSE_LOCATION, false) -> {
                Toast.makeText(
                    applicationContext,
                    "Coarse permission granted, positioning will not work",
                    Toast.LENGTH_LONG
                ).show()
            }

            else -> {
                Toast.makeText(
                    applicationContext,
                    "No location permission granted, positioning will not work",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private val uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OriientSdkTestAppTheme {
                MainScreen(uiState.collectAsState().value)
            }
        }
        checkPermissions()
    }

    @Composable
    private fun MainScreen(state: UiState) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center
            ) {
                Button(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                    enabled = !state.loginLoading,
                    onClick = {
                        if (state.isLoggedIn) {
                            logout()
                        } else {
                            login()
                        }
                    }) {
                    Text(text = if (state.isLoggedIn) "Log out" else "Log in")
                }
                Button(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                    enabled = !state.positioningLoading,
                    onClick = {
                        if (state.isPositioning) {
                            stopPositioning()
                        } else {
                            startPositioning()
                        }
                    }) {
                    Text(
                        text = if (state.isPositioning) {
                            "Stop positioning"
                        } else {
                            "Start positioning"
                        }
                    )
                }
            }
        }
    }

    private fun checkPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
            }

            else -> {
                locationPermissionRequest.launch(
                    arrayOf(
                        ACCESS_FINE_LOCATION,
                        ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun login() {
        uiState.update {
            it.copy(loginLoading = true)
        }
        IPSCore.login(
            "",
            BuildConfig.ORIIENT_API_KEY,
            "https://ips.oriient.me/",
            object : IPSLoginListener {
                override fun onError(p0: IPSError) {
                    uiState.update {
                        it.copy(isLoggedIn = false, loginLoading = false)
                    }
                }

                override fun onLogin(p0: MutableList<IPSSpace>) {
                    uiState.update {
                        it.copy(isLoggedIn = true, loginLoading = false)
                    }
                }
            }
        )
    }

    private fun logout() {
        uiState.update {
            it.copy(loginLoading = true)
        }
        IPSCore.logout(object : IPSLogoutListener {
            override fun onError(p0: IPSError) {
                uiState.update {
                    it.copy(isLoggedIn = false, loginLoading = false)
                }
            }

            override fun onLogout() {
                uiState.update {
                    it.copy(isLoggedIn = false, loginLoading = false)
                }
            }
        })
    }

    private fun startPositioning() {
        uiState.update {
            it.copy(positioningLoading = true)
        }
        IPSPositioning.startPositioning(
            "<BUILDING_ID>",
            null,
            null,
            true,
            object : IPSCompletionListener {
                override fun onError(p0: IPSError) {
                    uiState.update {
                        it.copy(isPositioning = false, positioningLoading = false)
                    }
                }

                override fun onCompleted() {
                    uiState.update {
                        it.copy(isPositioning = true, positioningLoading = false)
                    }
                }
            }
        )
    }

    private fun stopPositioning() {
        IPSPositioning.stopPositioning(object : IPSCompletionListener {
            override fun onError(p0: IPSError) {
                uiState.update {
                    it.copy(isPositioning = false, positioningLoading = false)
                }
            }

            override fun onCompleted() {
                uiState.update {
                    it.copy(isPositioning = false, positioningLoading = false)
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        stopPositioning()
        logout()
    }
}

data class UiState(
    val isLoggedIn: Boolean = false,
    val isPositioning: Boolean = false,
    val loginLoading: Boolean = false,
    val positioningLoading: Boolean = false
)