import managers.InMemoryTasksManager;

class InMemoryTasksManagerTest extends TasksManagerTest<InMemoryTasksManager> {

    @Override
    InMemoryTasksManager getTaskManager() {
        return new InMemoryTasksManager();
    }
}