package com.mhss.app.data.tools

import com.mhss.app.domain.model.Priority
import com.mhss.app.domain.model.SubTask
import com.mhss.app.domain.model.Task
import com.mhss.app.domain.model.TaskFrequency
import com.mhss.app.domain.use_case.GetTaskByIdUseCase
import com.mhss.app.domain.use_case.SearchTasksUseCase
import com.mhss.app.domain.use_case.UpdateTaskCompletedUseCase
import com.mhss.app.domain.use_case.UpsertTaskUseCase
import com.mhss.app.domain.use_case.UpsertTasksUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class TaskToolSetTest {

    private lateinit var taskToolSet: TaskToolSet
    
    private val upsertTask: UpsertTaskUseCase = mockk()
    private val upsertTasks: UpsertTasksUseCase = mockk()
    private val searchTasksByName: SearchTasksUseCase = mockk()
    private val getTask: GetTaskByIdUseCase = mockk()
    private val updateTaskCompletedUseCase: UpdateTaskCompletedUseCase = mockk()

    @Before
    fun setup() {
        taskToolSet = TaskToolSet(
            upsertTask = upsertTask,
            upsertTasks = upsertTasks,
            searchTasksByName = searchTasksByName,
            getTask = getTask,
            updateTaskCompletedUseCase = updateTaskCompletedUseCase
        )
    }

    @Test
    fun testSearchTasks() = runTest {
        val expectedTasks = listOf(
            Task(title = "Task 1", description = "Description 1", id = "task-1"),
            Task(title = "Task 2", description = "Description 2", id = "task-2")
        )
        coEvery { searchTasksByName("test").first() } returns expectedTasks
        
        val result = taskToolSet.searchTasks("test")
        
        assertNotNull("Result should not be null", result)
        assertEquals("Should return 2 tasks", 2, result.tasks.size)
        assertEquals("First task title should match", "Task 1", result.tasks[0].title)
        coVerify { searchTasksByName("test") }
    }

    @Test
    fun testCreateTask() = runTest {
        coEvery { upsertTask(any()) } returns true
        
        val result = taskToolSet.createTask(
            title = "Test Task",
            description = "Test Description",
            priority = Priority.HIGH,
            dueDate = "2025-01-15T10:00:00",
            subTasks = null,
            recurring = false,
            frequency = TaskFrequency.DAILY,
            frequencyAmount = 1
        )
        
        assertNotNull("Result should not be null", result)
        assertNotNull("Should return a task ID", result.createdTaskId)
        coVerify { upsertTask(any()) }
    }

    @Test
    fun testCreateTaskWithSubTasks() = runTest {
        val subTaskInputs = listOf(
            SubTaskInput(title = "Subtask 1", isCompleted = false),
            SubTaskInput(title = "Subtask 2", isCompleted = true)
        )
        coEvery { upsertTask(any()) } returns true
        
        val result = taskToolSet.createTask(
            title = "Test Task",
            description = "Test Description",
            priority = Priority.MEDIUM,
            dueDate = null,
            subTasks = subTaskInputs,
            recurring = false,
            frequency = TaskFrequency.WEEKLY,
            frequencyAmount = 2
        )
        
        assertNotNull("Result should not be null", result)
        assertNotNull("Should return a task ID", result.createdTaskId)
        coVerify { upsertTask(any()) }
    }

    @Test
    fun testUpdateTaskCompleted() = runTest {
        val task = Task(
            title = "Test Task",
            description = "Test Description",
            isCompleted = false,
            id = "task-123"
        )
        coEvery { getTask("task-123") } returns task
        
        taskToolSet.updateTaskCompleted("task-123", true)
        
        coVerify { getTask("task-123") }
        coVerify { updateTaskCompletedUseCase(task, true) }
    }

    @Test
    fun testCreateMultipleTasks() = runTest {
        val taskInputs = listOf(
            TaskInput(
                title = "Task 1",
                description = "Description 1",
                priority = Priority.LOW
            ),
            TaskInput(
                title = "Task 2",
                description = "Description 2",
                priority = Priority.HIGH
            )
        )
        coEvery { upsertTasks(any()) } returns Unit
        
        val result = taskToolSet.createMultipleTasks(taskInputs)
        
        assertNotNull("Result should not be null", result)
        assertEquals("Should return 2 task IDs", 2, result.createdTaskIds.size)
        coVerify { upsertTasks(any()) }
    }

    @Test
    fun testCreateMultipleTasksWithDueDates() = runTest {
        val taskInputs = listOf(
            TaskInput(
                title = "Task 1",
                description = "Description 1",
                priority = Priority.MEDIUM,
                dueDate = "2025-01-15T10:00:00"
            ),
            TaskInput(
                title = "Task 2",
                description = "Description 2",
                priority = Priority.LOW,
                dueDate = "2025-01-16T14:30:00"
            )
        )
        coEvery { upsertTasks(any()) } returns Unit
        
        val result = taskToolSet.createMultipleTasks(taskInputs)
        
        assertNotNull("Result should not be null", result)
        assertEquals("Should return 2 task IDs", 2, result.createdTaskIds.size)
        coVerify { upsertTasks(any()) }
    }
}
