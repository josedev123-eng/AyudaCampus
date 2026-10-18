package com.rojas.ayudacampus
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.rojas.ayudacampus.model.Rol
import com.rojas.ayudacampus.ui.navigation.NavAyuda
import com.rojas.ayudacampus.ui.theme.AyudaTheme
import com.rojas.ayudacampus.AyudaApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as AyudaApp

        setContent  {
            AyudaTheme(rol = Rol.ADMIN) {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    NavAyuda(app = app)
                }
            }
        }
    }
}