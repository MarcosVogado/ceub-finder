package com.example.ceubfinder.ui.screens.publico

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ceubfinder.data.database.CeubDatabaseHelper
import com.example.ceubfinder.data.model.Objeto
import com.example.ceubfinder.ui.components.ObjetoCard
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncioScreen(
    dbHelper: CeubDatabaseHelper,
    onNavigateToSearch: (String?, Long?) -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToInfo: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val categorias = remember { dbHelper.getAllCategorias() }
    val objetosRecentes = remember { dbHelper.getObjetosDisponiveis() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CEUB Finder", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateToInfo) {
                        Icon(Icons.Default.Info, contentDescription = "Informações")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Banner / Welcome
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Achados e Perdidos — CEUB",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Consulte objetos recolhidos no campus de forma rápida e prática. A retirada é feita presencialmente na central.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Search Bar Shortcut
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("O que você perdeu? (ex: fone, carteira)") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        Button(
                            onClick = { onNavigateToSearch(searchQuery.ifBlank { null }, null) },
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text("Buscar")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // Categories Shortcut (with working filter)
            item {
                Text(
                    text = "Categorias",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categorias) { categoria ->
                        FilterChip(
                            selected = false,
                            onClick = { onNavigateToSearch(null, categoria.id) },
                            label = { Text(categoria.nome) }
                        )
                    }
                }
            }

            // Recent Items header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Objetos Disponíveis Recentes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { onNavigateToSearch(null, null) }) {
                        Text("Ver todos")
                    }
                }
            }

            if (objetosRecentes.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhum objeto disponível no momento.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                items(objetosRecentes.take(5)) { objeto ->
                    ObjetoCard(
                        objeto = objeto,
                        onClick = { onNavigateToDetail(objeto.id) }
                    )
                }
            }

            // Discreet admin entry
            item {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = onNavigateToLogin,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Área do Funcionário (Login)")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscarScreen(
    dbHelper: CeubDatabaseHelper,
    initialQuery: String?,
    initialCategoryId: Long?,
    onNavigateToDetail: (Long) -> Unit
) {
    var searchQuery by remember { mutableStateOf(initialQuery ?: "") }
    val categorias = remember { dbHelper.getAllCategorias() }
    var selectedCategoriaId by remember { mutableStateOf<Long?>(initialCategoryId) }
    var localFilter by remember { mutableStateOf("") }

    val objetos = remember(searchQuery, selectedCategoriaId, localFilter) {
        dbHelper.getObjetosDisponiveis(
            querySearch = searchQuery.ifBlank { null },
            categoriaId = selectedCategoriaId,
            localFilter = localFilter.ifBlank { null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscar Objetos Perdidos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Pesquisar por nome ou descrição") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = localFilter,
                onValueChange = { localFilter = it },
                label = { Text("Filtrar por local (ex: biblioteca, bosque)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Category filter chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategoriaId == null,
                        onClick = { selectedCategoriaId = null },
                        label = { Text("Todas") }
                    )
                }
                items(categorias) { cat ->
                    FilterChip(
                        selected = selectedCategoriaId == cat.id,
                        onClick = { selectedCategoriaId = if (selectedCategoriaId == cat.id) null else cat.id },
                        label = { Text(cat.nome) }
                    )
                }
            }

            Text(
                text = "${objetos.size} objeto(s) encontrado(s)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(objetos) { objeto ->
                    ObjetoCard(
                        objeto = objeto,
                        onClick = { onNavigateToDetail(objeto.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalheObjetoScreen(
    dbHelper: CeubDatabaseHelper,
    objetoId: Long,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val objeto = remember(objetoId) { dbHelper.getObjetoById(objetoId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(objeto?.nome ?: "Detalhes do Objeto") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        if (objeto == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Objeto não encontrado.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Photo
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!objeto.fotoPath.isNullOrBlank()) {
                            AsyncImage(
                                model = File(objeto.fotoPath),
                                contentDescription = objeto.nome,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = "Sem fotografia cadastrada",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = objeto.nome,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        com.example.ceubfinder.ui.components.StatusBadge(status = objeto.status)
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DetailRow(label = "Categoria", value = objeto.categoriaNome ?: "Geral")
                            DetailRow(label = "Local específico", value = objeto.localEspecifico)
                            if (!objeto.pontoReferencia.isNullOrBlank()) {
                                DetailRow(label = "Ponto de referência", value = objeto.pontoReferencia)
                            }
                            if (!objeto.detalhesLocal.isNullOrBlank()) {
                                DetailRow(label = "Detalhes do local", value = objeto.detalhesLocal)
                            }
                            DetailRow(label = "Data encontrado", value = objeto.dataEncontrado)
                            DetailRow(label = "Data de cadastro", value = objeto.dataCadastro)
                        }
                    }
                }

                if (!objeto.descricao.isNullOrBlank()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Descrição pública",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = objeto.descricao,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                // In-person notice & Contact button
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Atenção: Retirada Presencial",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = "A confirmação da propriedade e a liberação do objeto ocorrem presencialmente na Central de Achados e Perdidos do CEUB.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )

                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:6139661234")
                                    }
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Ligar para a Central (Demonstrativo)")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformacoesScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Informações e Orientações") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "How funciona o CEUB Finder?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            item {
                Text(
                    text = "O CEUB Finder é um catálogo digital acadêmico para consulta de objetos encontrados e entregues na Central de Achados e Perdidos do CEUB.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Regras e Orientações Importantes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(text = "• A consulta é pública e aberta a todos os estudantes, sem necessidade de cadastro ou login.")
                        Text(text = "• Os objetos listados são aqueles fisicamente recebidos e registrados pela central.")
                        Text(text = "• A entrega de objetos encontrados por estudantes deve ser feita diretamente na central.")
                        Text(text = "• A verificação de propriedade e retirada é exclusivamente presencial.")
                        Text(text = "• Prazo estimado de até 1 dia útil para publicação após o recebimento físico (proposta orientativa do protótipo).")
                    }
                }
            }
        }
    }
}
