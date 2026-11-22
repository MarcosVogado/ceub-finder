package com.example.ceubfinder.ui.screens.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ceubfinder.data.database.CeubDatabaseHelper
import com.example.ceubfinder.data.model.Objeto
import com.example.ceubfinder.util.FileUtils
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginAdmScreen(
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("funcionario@ceub.br") }
    var senha by remember { mutableStateOf("123456") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Área do Funcionário - Login") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "Acesso Administrativo",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Atenção: A autenticação é simulada para fins acadêmicos. Qualquer e-mail e senha válidos permitirão o acesso.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("E-mail institucional") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = senha,
                        onValueChange = { senha = it },
                        label = { Text("Senha") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Button(
                        onClick = {
                            if (email.isBlank() || senha.isBlank()) {
                                errorMessage = "Preencha e-mail e senha."
                            } else {
                                onLoginSuccess()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Entrar")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardAdmScreen(
    dbHelper: CeubDatabaseHelper,
    onNavigateToCadastro: () -> Unit,
    onNavigateToGerenciamento: () -> Unit,
    onNavigateToHistorico: () -> Unit,
    onLogout: () -> Unit
) {
    val todosObjetos = remember { dbHelper.getAllObjetosAdmin() }
    val disponiveisCount = todosObjetos.count { it.status == "DISPONIVEL" }
    val devolvidosCount = todosObjetos.count { it.status == "DEVOLVIDO" }
    val totalCount = todosObjetos.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Painel Administrativo") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Sair")
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
                    text = "Indicadores da Central",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            // Indicators Grid / Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IndicatorCard(
                        title = "Disponíveis",
                        count = disponiveisCount.toString(),
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                    IndicatorCard(
                        title = "Devolvidos",
                        count = devolvidosCount.toString(),
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    IndicatorCard(
                        title = "Total",
                        count = totalCount.toString(),
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                }
            }

            // Action Buttons
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onNavigateToCadastro,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cadastrar Novo Objeto")
                    }

                    OutlinedButton(
                        onClick = onNavigateToGerenciamento,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.List, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gerenciar Objetos (Disponíveis / Devolver)")
                    }

                    OutlinedButton(
                        onClick = onNavigateToHistorico,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.History, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Histórico de Devolvidos")
                    }
                }
            }
        }
    }
}

@Composable
fun IndicatorCard(title: String, count: String, modifier: Modifier = Modifier, containerColor: androidx.compose.ui.graphics.Color) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = count, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(text = title, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroObjetoScreen(
    dbHelper: CeubDatabaseHelper,
    objetoIdToEdit: Long?,
    onSaveFinished: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val categorias = remember { dbHelper.getAllCategorias() }

    var nome by remember { mutableStateOf("") }
    var selectedCategoriaId by remember { mutableStateOf(categorias.firstOrNull()?.id ?: 1L) }
    var descricao by remember { mutableStateOf("") }
    var localEspecifico by remember { mutableStateOf("") }
    var pontoReferencia by remember { mutableStateOf("") }
    var detalhesLocal by remember { mutableStateOf("") }
    var dataEncontrado by remember { mutableStateOf("") }
    var fotoPath by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Load existing object if editing
    LaunchedEffect(objetoIdToEdit) {
        if (objetoIdToEdit != null && objetoIdToEdit > 0) {
            dbHelper.getObjetoById(objetoIdToEdit)?.let { obj ->
                nome = obj.nome
                selectedCategoriaId = obj.categoriaId
                descricao = obj.descricao ?: ""
                localEspecifico = obj.localEspecifico
                pontoReferencia = obj.pontoReferencia ?: ""
                detalhesLocal = obj.detalhesLocal ?: ""
                dataEncontrado = obj.dataEncontrado
                fotoPath = obj.fotoPath
            }
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val path = FileUtils.copyUriToInternalStorage(context, uri)
            if (path != null) {
                fotoPath = path
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (objetoIdToEdit == null) "Cadastrar Objeto" else "Editar Objeto") },
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
                // Photo selection
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!fotoPath.isNullOrBlank()) {
                            AsyncImage(
                                model = File(fotoPath!!),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.outline)
                        }
                    }

                    OutlinedButton(onClick = { photoPickerLauncher.launch("image/*") }) {
                        Text(if (fotoPath == null) "Selecionar Foto da Galeria" else "Alterar Foto")
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome do objeto * (ex: Fone Bluetooth)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // Category selector dropdown / chips
            item {
                Text(text = "Categoria *", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categorias.forEach { cat ->
                        FilterChip(
                            selected = selectedCategoriaId == cat.id,
                            onClick = { selectedCategoriaId = cat.id },
                            label = { Text(cat.nome) }
                        )
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = localEspecifico,
                    onValueChange = { localEspecifico = it },
                    label = { Text("Local específico * (ex: Biblioteca, Bosque, Bloco A)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = pontoReferencia,
                    onValueChange = { pontoReferencia = it },
                    label = { Text("Ponto de referência (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = dataEncontrado,
                    onValueChange = { dataEncontrado = it },
                    label = { Text("Data em que foi encontrado * (ex: 2026-05-15)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição pública / características gerais (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3
                )
            }

            if (errorMessage != null) {
                item {
                    Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                }
            }

            item {
                Button(
                    onClick = {
                        if (nome.isBlank() || localEspecifico.isBlank() || dataEncontrado.isBlank()) {
                            errorMessage = "Preencha todos os campos obrigatórios (*)."
                        } else {
                            if (objetoIdToEdit == null) {
                                dbHelper.insertObjeto(
                                    categoriaId = selectedCategoriaId,
                                    nome = nome,
                                    descricao = descricao.ifBlank { null },
                                    localEspecifico = localEspecifico,
                                    pontoReferencia = pontoReferencia.ifBlank { null },
                                    detalhesLocal = detalhesLocal.ifBlank { null },
                                    dataEncontrado = dataEncontrado,
                                    fotoPath = fotoPath
                                )
                            } else {
                                dbHelper.updateObjeto(
                                    id = objetoIdToEdit,
                                    categoriaId = selectedCategoriaId,
                                    nome = nome,
                                    descricao = descricao.ifBlank { null },
                                    localEspecifico = localEspecifico,
                                    pontoReferencia = pontoReferencia.ifBlank { null },
                                    detalhesLocal = detalhesLocal.ifBlank { null },
                                    dataEncontrado = dataEncontrado,
                                    fotoPath = fotoPath
                                )
                            }
                            onSaveFinished()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Salvar Registro")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GerenciamentoScreen(
    dbHelper: CeubDatabaseHelper,
    onNavigateToEdit: (Long) -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Disponíveis, 1: Devolvidos
    val statusFilter = if (selectedTab == 0) "DISPONIVEL" else "DEVOLVIDO"
    var objetos by remember { mutableStateOf(dbHelper.getAllObjetosAdmin(statusFilter)) }
    var showDeleteDialog by remember { mutableStateOf<Long?>(null) }
    var showDevolverDialog by remember { mutableStateOf<Long?>(null) }

    fun reload() {
        objetos = dbHelper.getAllObjetosAdmin(statusFilter)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gerenciamento de Objetos") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0; reload() },
                    text = { Text("Disponíveis") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1; reload() },
                    text = { Text("Devolvidos") }
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(objetos) { objeto ->
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = objeto.nome, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                com.example.ceubfinder.ui.components.StatusBadge(status = objeto.status)
                            }
                            Text(text = "Categoria: ${objeto.categoriaNome ?: "Geral"}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Local: ${objeto.localEspecifico}", style = MaterialTheme.typography.bodySmall)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (objeto.status == "DISPONIVEL") {
                                    TextButton(onClick = { showDevolverDialog = objeto.id }) {
                                        Text("Marcar Devolvido")
                                    }
                                }
                                TextButton(onClick = { onNavigateToEdit(objeto.id) }) {
                                    Text("Editar")
                                }
                                TextButton(onClick = { showDeleteDialog = objeto.id }) {
                                    Text("Excluir", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Devolver Dialog
    if (showDevolverDialog != null) {
        AlertDialog(
            onDismissRequest = { showDevolverDialog = null },
            title = { Text("Marcar como Devolvido") },
            text = { Text("Deseja alterar o status deste objeto para DEVOLVIDO? Ele sairá da listagem pública.") },
            confirmButton = {
                TextButton(onClick = {
                    dbHelper.marcarComoDevolvido(showDevolverDialog!!)
                    showDevolverDialog = null
                    reload()
                }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDevolverDialog = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Delete Dialog
    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Confirmar Exclusão") },
            text = { Text("Tem certeza que deseja excluir permanentemente este registro do banco de dados?") },
            confirmButton = {
                TextButton(onClick = {
                    dbHelper.excluirObjeto(showDeleteDialog!!)
                    showDeleteDialog = null
                    reload()
                }) {
                    Text("Excluir", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricoScreen(
    dbHelper: CeubDatabaseHelper,
    onBack: () -> Unit
) {
    val devolvidos = remember { dbHelper.getAllObjetosAdmin("DEVOLVIDO") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Histórico de Objetos Devolvidos") },
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (devolvidos.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Nenhum objeto devolvido no histórico.")
                    }
                }
            } else {
                items(devolvidos) { objeto ->
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = objeto.nome, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                com.example.ceubfinder.ui.components.StatusBadge(status = objeto.status)
                            }
                            Text(text = "Categoria: ${objeto.categoriaNome ?: "Geral"}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Encontrado em: ${objeto.dataEncontrado}", style = MaterialTheme.typography.bodySmall)
                            Text(text = "Devolvido em: ${objeto.dataDevolucao ?: "Data não registrada"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}
