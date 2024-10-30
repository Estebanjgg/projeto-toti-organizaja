@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.todolist

import TaskViewModel
import TaskViewModelFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolist.data.Task
import com.example.todolist.data.TaskDatabase
import com.example.todolist.data.TaskRepository
import com.example.todolist.ui.theme.ToDoListTheme
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter

class MainActivity : ComponentActivity() {

    private val taskViewModel: TaskViewModel by viewModels {
        val repository = TaskRepository(TaskDatabase.getDatabase(this).taskDao())
        TaskViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            ToDoListTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "main_screen") {
                    composable("main_screen") {
                        MainScreen(navController)
                    }
                    composable("todo_list_screen") {
                        ToDoListScreen(
                            viewModel = taskViewModel,
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = { isDarkTheme = !isDarkTheme }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(25.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "OrganizaJá.",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .offset(y = (-16).dp)
        )

        Image(
            painter = rememberAsyncImagePainter(model = R.drawable.img3),
            contentDescription = "Imagem do projeto",
            modifier = Modifier.size(350.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { navController.navigate("todo_list_screen") },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .padding(8.dp)
                .scale(1.1f)
        ) {
            Text("Adicionar tarefas", color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@Composable
fun ToDoListScreen(
    viewModel: TaskViewModel,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    var taskName by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var currentFilter by remember { mutableStateOf("Show All") }
    val allTasks by viewModel.allTasks.observeAsState(initial = emptyList())
    val filteredTasks = when (currentFilter) {
        "Show All" -> allTasks
        "Show Only Done" -> allTasks.filter { it.isCompleted }
        "Show Only Pending" -> allTasks.filter { !it.isCompleted }
        else -> allTasks
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "OrganizaJá",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                actions = {
                    IconButton(onClick = { onToggleTheme() }) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "alterar o tema",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {

                TextField(
                    value = taskName,
                    onValueChange = {
                        taskName = it
                        showError = taskName.length <= 1
                    },
                    label = { Text("Nova tarefa") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                if (showError) {
                    Text(
                        text = "O nome da tarefa deve ter mais de um caractere",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp, start = 16.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(20.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            if (taskName.length > 1) {
                                viewModel.insert(Task(title = taskName))
                                taskName = ""
                                showError = false
                            } else {
                                showError = true
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Adicionar tarefa", color = MaterialTheme.colorScheme.onPrimary)
                    }
                    FilterMenu(onFilterSelected = { filter -> currentFilter = filter })
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    items(filteredTasks) { task ->
                        AnimatedTaskItem(
                            task = task,
                            onTaskAction = { action ->
                                when (action) {
                                    is TaskAction.Update -> viewModel.update(task.copy(title = action.updatedName))
                                    TaskAction.Delete -> viewModel.delete(task)
                                    TaskAction.MarkDone -> viewModel.markDone(task)
                                }
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun FilterMenu(onFilterSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filter",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Exibir tudo") },
                onClick = {
                    onFilterSelected("Show All")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Mostrar Finalizado") },
                onClick = {
                    onFilterSelected("Show Only Done")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Mostrar Pendentes") },
                onClick = {
                    onFilterSelected("Show Only Pending")
                    expanded = false
                }
            )

        }
    }
}

@Composable
fun AnimatedTaskItem(task: Task, onTaskAction: (TaskAction) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var updatedTaskName by remember { mutableStateOf(TextFieldValue(task.title)) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Opções de tarefas", color = MaterialTheme.colorScheme.onBackground) },
            text = {
                Column {
                    Button(
                        onClick = {
                            showDialog = false
                            onTaskAction(TaskAction.MarkDone)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Marcar como concluído", color = MaterialTheme.colorScheme.onPrimary) }

                    Button(
                        onClick = {
                            showDialog = false
                            onTaskAction(TaskAction.Delete)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Excluir tarefa", color = MaterialTheme.colorScheme.onError) }

                    TextField(
                        value = updatedTaskName,
                        onValueChange = {
                            updatedTaskName = it
                            showError = it.text.length <= 1
                        },
                        label = { Text("Atualizar tarefa") },
                        colors = TextFieldDefaults.textFieldColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (showError) {
                        Text(
                            "O nome da tarefa deve ter mais de um caractere",
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Button(
                        onClick = {
                            if (!showError) {
                                showDialog = false
                                onTaskAction(TaskAction.Update(updatedName = updatedTaskName.text))
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        enabled = !showError,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Atualizar tarefa", color = MaterialTheme.colorScheme.onPrimary) }
                }
            },
            confirmButton = {},
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        )
    }

    val taskColor = if (task.isCompleted) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }
    val taskStatus = if (task.isCompleted) "Finalizado" else "Pendente"
    val statusColor = if (task.isCompleted) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(taskColor, RoundedCornerShape(8.dp))
            .clickable { showDialog = true }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            task.title,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Start,
            fontSize = 18.sp
        )
        Text(
            taskStatus,
            color = statusColor,
            modifier = Modifier.padding(start = 8.dp),
            fontSize = 14.sp
        )
    }
}

sealed class TaskAction {
    object Delete : TaskAction()
    object MarkDone : TaskAction()
    data class Update(val updatedName: String) : TaskAction()
}
