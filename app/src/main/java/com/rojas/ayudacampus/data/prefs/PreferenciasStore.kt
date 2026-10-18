package com.rojas.ayudacampus.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("ajustes")

class PreferenciasStore(private val context: Context) {

    companion object {
        val PERFIL_ACTIVO = stringPreferencesKey("perfilActivo")
        val USUARIO_ID = intPreferencesKey("usuarioId")
        val MODO_OSCURO = booleanPreferencesKey("modoOscuro")
        val NOTIFICACIONES = booleanPreferencesKey("notificaciones")
        val RECIBIR_TICKETS = booleanPreferencesKey("recibirTickets")
    }

    // ---------- Lectura: un Flow por preferencia ----------

    val perfilActivo: Flow<String> = context.dataStore.data.map { it[PERFIL_ACTIVO] ?: "" }

    val usuarioId: Flow<Int> = context.dataStore.data.map { it[USUARIO_ID] ?: 0 }

    val modoOscuro: Flow<Boolean> = context.dataStore.data.map { it[MODO_OSCURO] ?: false }

    val notificaciones: Flow<Boolean> = context.dataStore.data.map { it[NOTIFICACIONES] ?: true }

    val recibirTickets: Flow<Boolean> = context.dataStore.data.map { it[RECIBIR_TICKETS] ?: true }

    // ---------- Escritura (suspend) ----------

    suspend fun guardarSesion(rol: String, usuarioId: Int) {
        context.dataStore.edit {
            it[PERFIL_ACTIVO] = rol
            it[USUARIO_ID] = usuarioId
        }
    }

    suspend fun guardarModoOscuro(b: Boolean) {
        context.dataStore.edit { it[MODO_OSCURO] = b }
    }

    suspend fun guardarNotificaciones(b: Boolean) {
        context.dataStore.edit { it[NOTIFICACIONES] = b }
    }

    suspend fun guardarRecibirTickets(b: Boolean) {
        context.dataStore.edit { it[RECIBIR_TICKETS] = b }
    }

    suspend fun cerrarSesion() {
        context.dataStore.edit {
            it[PERFIL_ACTIVO] = ""
            it[USUARIO_ID] = 0
        }
    }
}
