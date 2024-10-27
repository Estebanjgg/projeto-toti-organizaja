import TaskViewModel
import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todolist.ToDoListScreen
import com.example.todolist.data.TaskDao
import com.example.todolist.data.TaskDatabase
import com.example.todolist.data.TaskRepository
import com.example.todolist.jetpacktest.ToDoListSteps
import com.example.todolist.jetpacktest.pageViews.ToDoListPage
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration

@RunWith(AndroidJUnit4::class)
class ToDoListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var steps: ToDoListSteps
    private lateinit var taskDao: TaskDao
    private lateinit var taskDatabase: TaskDatabase
    private lateinit var taskRepository: TaskRepository
    private lateinit var taskViewModel: TaskViewModel

    @Before
    fun setup() {
        // Get the application context
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Create an in-memory Room database
        taskDatabase = Room.inMemoryDatabaseBuilder(
            context, TaskDatabase::class.java
        ).allowMainThreadQueries().build()

        // Get the TaskDao from the database
        taskDao = taskDatabase.taskDao()

        // Create the repository and ViewModel with the real TaskDao
        taskRepository = TaskRepository(taskDao)
        taskViewModel = TaskViewModel(taskRepository)
        val page = ToDoListPage(composeTestRule)
        steps = ToDoListSteps(page)
    }

    @After
    fun tearDown() {
        // Close the database after tests to prevent memory leaks
        taskDatabase.close()
    }

    @Test
    fun testAddTask() {
        // Set the Composable content with the real ViewModel
        composeTestRule.setContent {
            ToDoListScreen(
                viewModel = taskViewModel,
                isDarkTheme = false,
                onToggleTheme = {} // Función vacía para el cambio de tema
            )
        }
        val task = "Buy Milk"
        steps.userAddingNewTask(task)
        steps.verifyTaskIsExist(task)
    }

    @Test
    fun testDeleteTask() {
        // Add a task first
        composeTestRule.setContent {
            ToDoListScreen(
                viewModel = taskViewModel,
                isDarkTheme = false,
                onToggleTheme = {}
            )
        }
        val task = "Go to the Gym"
        steps.userAddingNewTask(task)
        steps.userDeleteTask(task)
        steps.verifyTaskIsDeleted(task)
    }

    @Test
    fun testMarkTaskAsDone() {
        // Add a task first
        composeTestRule.setContent {
            ToDoListScreen(
                viewModel = taskViewModel,
                isDarkTheme = false,
                onToggleTheme = {}
            )
        }
        val task = "Do dishes"
        steps.userAddingNewTask(task)
        steps.userMarkTaskAsDone(task)
        composeTestRule.waitForIdle()
        steps.verifyTaskIsDone()
    }

    @Test
    fun testFilterDoneTasks() {
        // Add a task and mark it as done
        composeTestRule.setContent {
            ToDoListScreen(
                viewModel = taskViewModel,
                isDarkTheme = false,
                onToggleTheme = {}
            )
        }
        val task = "Complete Assignment"
        steps.userAddingNewTask(task)
        steps.userMarkTaskAsDone(task)
        steps.verifyAllDoneFilter(task)
    }

    @Test
    fun testShowAllTasks() {
        // Add two tasks: one marked as done, the other not
        composeTestRule.setContent {
            ToDoListScreen(
                viewModel = taskViewModel,
                isDarkTheme = false,
                onToggleTheme = {}
            )
        }
        composeTestRule.waitForIdle()
        val task1 = "Go for a walk"
        val task2 = "Play Soccer"
        steps.userAddingNewTask(task1)
        steps.userAddingNewTask(task2)
        steps.userMarkTaskAsDone(task2)
        steps.setToShowAllFilter()
        steps.verifyAllTasksPresent(task1, task2)
    }

    @Test
    fun testShowAllExceptDone() {
        // Add two tasks: one marked as done, the other not
        composeTestRule.setContent {
            ToDoListScreen(
                viewModel = taskViewModel,
                isDarkTheme = false,
                onToggleTheme = {}
            )
        }
        composeTestRule.waitForIdle()
        val task1 = "Go for a walk"
        val task2 = "Play Soccer"
        steps.userAddingNewTask(task1)
        steps.userAddingNewTask(task2)
        steps.userMarkTaskAsDone(task2)
        steps.setToShowAllExcludeDone()
        steps.verifyTaskExist(task1)
        steps.verifyTaskDoesNotExist(task2)
    }
}
