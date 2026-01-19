package com.chifunt.chromaticharptabs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.chifunt.chromaticharptabs.ui.theme.ChromaticHarpTabsTheme
import com.chifunt.chromaticharptabs.ui.navigation.ChromaticHarpTabsApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChromaticHarpTabsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ChromaticHarpTabsApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
