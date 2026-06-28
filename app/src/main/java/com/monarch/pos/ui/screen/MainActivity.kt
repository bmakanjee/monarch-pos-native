package com.monarch.pos.ui.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.monarch.pos.ui.theme.MonarchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MonarchTheme {
                MonarchNavGraph()
            }
        }
    }
}
