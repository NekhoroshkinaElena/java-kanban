import managers.Managers;
import managers.TaskManager;
import tasks.*;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        Task task = new Task("Помыть пол", "помыть пол с хлоркой в туалете");
        manager.createTask(task);
        manager.createTask(new Task("Приготовить ужин",
                "сварить борщ из ингридиентов в холодильнике"));
        Epic epic1 = new Epic("epic1", "epic1Description");
        manager.createEpic(epic1);
        Subtask subtask = new Subtask("epic1Subtask1",
                "epic1SubtaskDescription1", epic1);
        manager.createSubtask(epic1, subtask);
        manager.createSubtask(epic1, new Subtask("epic1Subtask2",
                "epic1SubtaskDescription2", epic1));
        Epic epic2 = new Epic("epic2", "epic2Description");
        manager.createEpic(epic2);
        manager.createSubtask(epic2, new Subtask("epic2Subtask1",
                "epic2SubtaskDescription1", epic1));
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());
        Task task1 = new Task("записаться ко врачу",
                "записаться к стоматологу", Status.IN_PROGRESS);
        task1.setId(task.getId());
        manager.updateTask(task1);
        System.out.println(manager.getTasks());
        Subtask subtask1 = new Subtask(subtask.getName(), subtask.getDescription(),
                Status.IN_PROGRESS, epic1);
        subtask1.setId(subtask.getId());
        manager.updateSubtask(subtask1);
        System.out.println(epic1);
        manager.removeTask(0);
        manager.removeEpic(5);
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());
        System.out.println();

        Task task2 = new Task("Покушать", "съесть мамины блинчики");
        manager.createTask(task2);

        manager.getEpicByID(epic1.getId());
        System.out.println(manager.getHistory());
        System.out.println();
        manager.getSubtaskByID(subtask.getId());
        System.out.println(manager.getHistory());
        System.out.println();
        manager.getTaskByID(task2.getId());
        System.out.println(manager.getHistory());
    }
}
