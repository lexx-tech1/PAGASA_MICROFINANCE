package com.pagasa.microfinance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.pagasa.microfinance.data.repository.MockPagasaRepository
import com.pagasa.microfinance.ui.navigation.PagasaApp
import com.pagasa.microfinance.ui.theme.PagasaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PagasaRoot() }
    }
}

@Composable
private fun PagasaRoot() {
    PagasaTheme {
        PagasaApp(repository = MockPagasaRepository())
    }
}
