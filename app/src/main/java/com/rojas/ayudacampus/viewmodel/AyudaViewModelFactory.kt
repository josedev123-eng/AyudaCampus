package com.rojas.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rojas.ayudacampus.data.local.entity.Usuario
import com.rojas.ayudacampus.data.prefs.PreferenciasStore
import com.rojas.ayudacampus.data.repository.AyudaRepository

class AyudaViewModelFactory(
    private val repo: AyudaRepository,
    private val prefs: PreferenciasStore,
    private val usuario: Usuario? = null,
    private val ticketId: Int = 0
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val vm: ViewModel = when {
            modelClass.isAssignableFrom(CategoriasViewModel::class.java) -> CategoriasViewModel(repo)
            modelClass.isAssignableFrom(SesionViewModel::class.java) -> SesionViewModel(repo, prefs)
            modelClass.isAssignableFrom(MisTicketsViewModel::class.java) -> MisTicketsViewModel(repo, usuario!!)
            modelClass.isAssignableFrom(NuevoTicketViewModel::class.java) -> NuevoTicketViewModel(repo, usuario!!)
            modelClass.isAssignableFrom(TicketDetalleViewModel::class.java) -> TicketDetalleViewModel(repo, ticketId, usuario!!)
            modelClass.isAssignableFrom(PanelViewModel::class.java) -> PanelViewModel(repo)
            modelClass.isAssignableFrom(BandejaViewModel::class.java) -> BandejaViewModel(repo)
            modelClass.isAssignableFrom(AtenderViewModel::class.java) -> AtenderViewModel(repo, ticketId, usuario!!)
            else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
        return vm as T
    }
}
