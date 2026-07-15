package com.entrecheckpoints.savefile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.entrecheckpoints.savefile.ui.SaveFileApp
import com.entrecheckpoints.savefile.ui.SaveFileViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: SaveFileViewModel = viewModel()
            SaveFileApp(viewModel)
        }
    }
}
