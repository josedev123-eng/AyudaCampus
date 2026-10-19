package com.rojas.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rojas.ayudacampus.data.local.entity.Usuario
import com.rojas.ayudacampus.data.prefs.PreferenciasStore
import com.rojas.ayudacampus.data.repository.AyudaRepository
import com.rojas.ayudacampus.model.Rol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SesionViewModel(
    private val repo: AyudaRepository,
    private val prefs: PreferenciasStore
) : ViewModel() {

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // ---------- Preferencias (DataStore) ----------

    val modoOscuro: StateFlow<Boolean> = prefs.modoOscuro
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val notificaciones: StateFlow<Boolean> = prefs.notificaciones
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val recibirTickets: StateFlow<Boolean> = prefs.recibirTickets
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    // ---------- Login ----------

    fun login(correo: String, clave: String, rolSeleccionado: Rol) {
        // 1. Campos vacíos
        if (correo.isBlank() || clave.isBlank()) {
            _error.value = "Completa el correo y la contraseña"
            return
        }
        viewModelScope.launch {
            val usuario = repo.login(correo.trim().lowercase(), clave.trim())
            if (usuario == null) {
                // 2. Credenciales incorrectas
                _error.value = "Correo o contraseña incorrectos"
            } else if (usuario.rol != rolSeleccionado.name) {
                // 3. El rol no coincide con la pestaña elegida
                _error.value = if (rolSeleccionado == Rol.CLIENTE) {
                    "Esta cuenta no es de estudiante, elige Soporte TI"
                } else {
                    "Esta cuenta no es de Soporte TI, elige Estudiante"
                }
            } else {
                // Éxito: se guarda el perfil en DataStore
                _error.value = null
                prefs.guardarSesion(usuario.rol, usuario.id)
                _usuarioActual.value = usuario
            }
        }
    }

    // La usa el Splash: devuelve el usuario guardado o null si nadie inició sesión
    fun restaurarSesion(alTerminar: (Usuario?) -> Unit) {
        viewModelScope.launch {
            val perfil = prefs.perfilActivo.first()
            val usuario = if (perfil.isEmpty()) null else repo.obtenerUsuario(prefs.usuarioId.first())
            _usuarioActual.value = usuario
            alTerminar(usuario)
        }
    }

    // Modo práctica: guarda el otro perfil y avisa para navegar a su grafo
    fun cambiarPerfil(alTerminar: (Usuario) -> Unit) {
        val actual = _usuarioActual.value ?: return
        val otroRol = if (actual.rol == Rol.ADMIN.name) Rol.CLIENTE else Rol.ADMIN
        viewModelScope.launch {
            val otro = repo.obtenerUsuarioPorRol(otroRol) ?: return@launch
            prefs.guardarSesion(otro.rol, otro.id)
            _usuarioActual.value = otro
            alTerminar(otro)
        }
    }

    // Borra el perfil guardado
    fun cerrarSesion() {
        _usuarioActual.value = null
        viewModelScope.launch { prefs.cerrarSesion() }
    }

    fun guardarModoOscuro(b: Boolean) {
        viewModelScope.launch { prefs.guardarModoOscuro(b) }
    }

    fun guardarNotificaciones(b: Boolean) {
        viewModelScope.launch { prefs.guardarNotificaciones(b) }
    }

    fun guardarRecibirTickets(b: Boolean) {
        viewModelScope.launch { prefs.guardarRecibirTickets(b) }
    }

    fun limpiarError() {
        _error.value = null
    }
}