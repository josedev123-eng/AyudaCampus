package com.rojas.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rojas.ayudacampus.data.repository.AyudaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class FortalezaClave {
    DEBIL,
    MEDIA,
    FUERTE
}

data class RequisitoClave(
    val descripcion: String,
    val cumplido: Boolean
)

data class ErroresRegistro(
    val nombre: String? = null,
    val correo: String? = null,
    val clave: String? = null,
    val confirmarClave: String? = null
)

class RegistroViewModel(private val repo: AyudaRepository) : ViewModel() {

    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre

    private val _correo = MutableStateFlow("")
    val correo: StateFlow<String> = _correo

    private val _clave = MutableStateFlow("")
    val clave: StateFlow<String> = _clave

    private val _confirmarClave = MutableStateFlow("")
    val confirmarClave: StateFlow<String> = _confirmarClave

    private val _errores = MutableStateFlow(ErroresRegistro())
    val errores: StateFlow<ErroresRegistro> = _errores

    private val _registrado = MutableStateFlow(false)
    val registrado: StateFlow<Boolean> = _registrado

    private val _fortaleza = MutableStateFlow(FortalezaClave.DEBIL)
    val fortaleza: StateFlow<FortalezaClave> = _fortaleza

    private val _requisitosClave = MutableStateFlow<List<RequisitoClave>>(emptyList())
    val requisitosClave: StateFlow<List<RequisitoClave>> = _requisitosClave

    init {
        actualizarCalculosClave()
    }

    fun cambiarNombre(v: String) {
        _nombre.value = v
        _errores.value = _errores.value.copy(nombre = null)
        actualizarCalculosClave()
    }

    fun cambiarCorreo(v: String) {
        _correo.value = v
        _errores.value = _errores.value.copy(correo = null)
        actualizarCalculosClave()
    }

    fun cambiarClave(v: String) {
        _clave.value = v
        _errores.value = _errores.value.copy(clave = null)
        actualizarCalculosClave()
    }

    fun cambiarConfirmarClave(v: String) {
        _confirmarClave.value = v
        _errores.value = _errores.value.copy(confirmarClave = null)
    }

    private fun actualizarCalculosClave() {
        val c = _clave.value
        val nom = _nombre.value
        val cor = _correo.value

        val req1 = RequisitoClave("Mínimo 8 caracteres", c.length >= 8)
        val req2 = RequisitoClave("Al menos una letra mayúscula", c.any { it.isUpperCase() })
        val req3 = RequisitoClave("Al menos una letra minúscula", c.any { it.isLowerCase() })
        val req4 = RequisitoClave("Al menos un número", c.any { it.isDigit() })
        val req5 = RequisitoClave("Al menos un símbolo (!@#$%._-)", c.any { !it.isLetterOrDigit() })

        val parteCorreo = if (cor.contains("@")) cor.substringBefore("@").trim().lowercase() else ""
        val partesNombre = nom.trim().lowercase().split("\\s+".toRegex()).filter { it.length >= 3 }
        val cLower = c.lowercase()

        val contieneNombre = partesNombre.any { cLower.contains(it) }
        val contieneCorreo = parteCorreo.length >= 3 && cLower.contains(parteCorreo)
        val noContieneDatos = c.isNotBlank() && !contieneNombre && !contieneCorreo

        val req6 = RequisitoClave("No contener tu nombre ni correo", noContieneDatos)

        val lista = listOf(req1, req2, req3, req4, req5, req6)
        _requisitosClave.value = lista

        val cumplidos = lista.count { it.cumplido }
        _fortaleza.value = when {
            cumplidos == 6 -> FortalezaClave.FUERTE
            cumplidos >= 3 -> FortalezaClave.MEDIA
            else -> FortalezaClave.DEBIL
        }
    }

    fun registrar() {
        val nom = nombre.value.trim()
        val cor = correo.value.trim().lowercase()
        val cla = clave.value
        val conf = confirmarClave.value

        var errNombre: String? = null
        var errCorreo: String? = null
        var errClave: String? = null
        var errConfirma: String? = null

        if (nom.isBlank()) {
            errNombre = "El nombre es obligatorio"
        }

        if (cor.isBlank()) {
            errCorreo = "El correo es obligatorio"
        } else if (!cor.contains("@") || !cor.substringAfter("@", "").contains(".")) {
            errCorreo = "Correo no válido"
        }

        val todasLasReglasCumplidas = requisitosClave.value.all { it.cumplido }
        if (!todasLasReglasCumplidas) {
            errClave = "La contraseña no cumple los requisitos"
        }

        if (conf != cla) {
            errConfirma = "Las contraseñas no coinciden"
        }

        if (errNombre != null || errCorreo != null || errClave != null || errConfirma != null) {
            _errores.value = ErroresRegistro(
                nombre = errNombre,
                correo = errCorreo,
                clave = errClave,
                confirmarClave = errConfirma
            )
            return
        }

        viewModelScope.launch {
            val exito = repo.registrar(nom, cor, cla)
            if (!exito) {
                _errores.value = ErroresRegistro(correo = "Ese correo ya está registrado")
            } else {
                _registrado.value = true
            }
        }
    }
}
