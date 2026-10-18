package com.rojas.ayudacampus.ui.soporte

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.rojas.ayudacampus.data.local.entity.Categoria
import com.rojas.ayudacampus.viewmodel.CategoriasViewModel

@Composable
fun CategoriasScreen(vm: CategoriasViewModel, onEditar: (Int) -> Unit) {
    val categorias by vm.categorias.collectAsState()
    LazyColumn {
        items(categorias, key = { it.id }) { c ->
            CategoriaItem(c, onToggle = { vm.alternar(c) }, onEditar = { onEditar(c.id) })
        }
    }
}

@Composable
fun CategoriaItem(c: Categoria, onToggle: () -> Unit, onEditar: () -> Unit) {
    Row {
        Text(c.nombre)
        Switch(checked = c.activa, onCheckedChange = { onToggle() })
        TextButton(onClick = onEditar) { Text("Editar") }
    }
}
