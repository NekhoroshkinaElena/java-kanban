import tasks.*;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task = new Task(manager.getUniqueID(), "Помыть пол", "помыть пол с хлоркой в туалете");
        manager.createTask(task);
        manager.createTask(new Task(manager.getUniqueID(), "Приготовить ужин",
                "сварить борщ из ингридиентов в холодильнике"));
        Epic epic1 = new Epic(manager.getUniqueID(), "epic1", "epic1Description");
        manager.createEpic(epic1);
        Subtask subtask = new Subtask(manager.getUniqueID(), "epic1Subtask1",
                "epic1SubtaskDescription1", epic1);
        manager.createSubtask(epic1, subtask);
        manager.createSubtask(epic1, new Subtask(manager.getUniqueID(), "epic1Subtask2",
                "epic1SubtaskDescription2", epic1));
        Epic epic2 = new Epic(manager.getUniqueID(), "epic2", "epic2Description");
        manager.createEpic(epic2);
        manager.createSubtask(epic2, new Subtask(manager.getUniqueID(), "epic2Subtask1",
                "epic2SubtaskDescription1", epic1));
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());
        manager.updateTask(new Task(task.getId(), "Поплакать",
                "Потому что ничего не получается", Status.IN_PROGRESS));
        System.out.println(manager.getTasks());
        manager.updateSubtask(new Subtask(subtask.getId(), subtask.getName(), subtask.getDescription(),
                Status.IN_PROGRESS, epic1));
        System.out.println(epic1);
        manager.removeTask(0);
        manager.removeEpic(5);
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());

    }
}
