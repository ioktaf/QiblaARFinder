package com.qiblaarfinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.qiblaarfinder.ui.MainViewModel
import com.qiblaarfinder.ui.QiblaApp
import com.qiblaarfinder.ui.theme.QiblaARFinderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (application as QiblaFinderApplication).appContainer

        setContent {
            QiblaARFinderTheme {
                val viewModel: MainViewModel = viewModel(
                    factory = MainViewModel.Factory(appContainer),
                )
                QiblaApp(viewModel = viewModel)
            }
        }
    }
}

