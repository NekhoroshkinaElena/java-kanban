package managers;

class InMemoryTasksManagerTest extends TasksManagerTest<InMemoryTasksManager> {

    @Override
    InMemoryTasksManager getTaskManager() {
        return new InMemoryTasksManager();
    }
}