package com.example.todolist.jetpacktest.pageViews

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput

class ToDoListPage(private val composeTestRule: ComposeTestRule) {

    // Locators for the elements
    val newTaskInput = composeTestRule.onNodeWithText("New Task")
    val addTaskButton = composeTestRule.onNodeWithText("Add Task")
    val markAsDoneButton = composeTestRule.onNodeWithText("Mark as Done")
    val deleteTaskButton = composeTestRule.onNodeWithText("Delete Task")
    val filterButton = composeTestRule.onNodeWithTag("filterButton")
    val showOnlyDoneFilter = composeTestRule.onNodeWithTag("showOnlyDoneFilter")
    val showAllFilter = composeTestRule.onNodeWithTag("showAllFilter")
    val excludeDoneFilter = composeTestRule.onNodeWithTag("excludeDoneFilter")

    // Actions on the elements
    fun addNewTask(taskName: String) {
        newTaskInput.performTextInput(taskName)
        addTaskButton.performClick()
    }

    fun markTaskAsDone(taskName: String) {
        composeTestRule.onNodeWithText(taskName).performClick()
        markAsDoneButton.performClick()
    }

    fun deleteTask(taskName: String){
        composeTestRule.onNodeWithText(taskName).performClick()
        deleteTaskButton.performClick()
    }

    fun assertTaskExists(taskName: String) {
        composeTestRule.onNodeWithText(taskName).assertExists()
    }

    fun setFilterTo(filter: TaskFilter) {
        when (filter) {
            TaskFilter.SHOW_ALL -> {
                showAllFilter.performClick()
            }
            TaskFilter.SHOW_ONLY_DONE -> {
               showOnlyDoneFilter.performClick()
            }
            TaskFilter.EXCLUDE_DONE -> {
                excludeDoneFilter.performClick()
            }
        }
    }
    fun assertTaskNotExists(taskName: String) {
        composeTestRule.onNodeWithText(taskName).assertDoesNotExist()
    }
    fun clickOnFilter(){
        filterButton.performClick()
    }

    fun assertTaskMarkedAsDone() {
        composeTestRule.onNodeWithContentDescription("Task Done").assertExists()
    }

    enum class TaskFilter {
        SHOW_ALL,
        SHOW_ONLY_DONE,
        EXCLUDE_DONE
    }
}
