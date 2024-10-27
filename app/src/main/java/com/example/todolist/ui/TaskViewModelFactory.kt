import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todolist.data.TaskRepository

class TaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    // The `TaskViewModelFactory` class implements `ViewModelProvider.Factory`.
    // This factory is responsible for creating instances of `TaskViewModel`, ensuring that the required `TaskRepository` dependency is provided.

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // The `create` method is overridden from `ViewModelProvider.Factory`.
        // It is responsible for creating and returning the ViewModel instance.

        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            // The `isAssignableFrom` check ensures that the ViewModel class being requested is `TaskViewModel`.

            @Suppress("UNCHECKED_CAST")
            // Suppresses unchecked cast warning because we know the type is correct.
            return TaskViewModel(repository) as T
            // Creates a new instance of `TaskViewModel`, passing the `TaskRepository` as a parameter.
            // The `as T` cast is necessary to return the ViewModel with the correct generic type.
        }

        throw IllegalArgumentException("Unknown ViewModel class")
        // If the ViewModel class is not `TaskViewModel`, an `IllegalArgumentException` is thrown.
        // This ensures that only `TaskViewModel` instances are created by this factory.
    }
}
