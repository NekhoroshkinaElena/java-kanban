import managers.*;
import tasks.*;

public class Main {

    public static void main(String[] args) {
        TasksManager manager = Managers.getDefault();

        Task task1 = new Task("Task1", "description Task1");
        Task task2 = new Task("Task2", "description Task2");
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic1 = new Epic("Epic1", "description Epic");
        Subtask subtask1 = new Subtask("Subtask1", "description Subtask1", epic1);
        Subtask subtask2 = new Subtask("Subtask2", "description Subtask2", epic1);
        Subtask subtask3 = new Subtask("Subtask3", "description Subtask3", epic1);
        manager.createEpic(epic1);
        manager.createSubtask(epic1, subtask1);
        manager.createSubtask(epic1, subtask2);
        manager.createSubtask(epic1, subtask3);

        Epic epic2 = new Epic("Epic2", "description Epic2");
        manager.createEpic(epic2);

        System.out.println(manager.getEpicByID(epic1.getId()));
        System.out.println(manager.getSubtaskByID(subtask1.getId()));
        System.out.println(manager.getTaskByID(task1.getId()));
        System.out.println();
        System.out.println(manager.getHistory());
        System.out.println();

        System.out.println(manager.getSubtaskByID(subtask1.getId()));
        System.out.println(manager.getEpicByID(epic1.getId()));
        System.out.println(manager.getTaskByID(task1.getId()));
        System.out.println();
        System.out.println(manager.getHistory());
        System.out.println();

        System.out.println(manager.getTaskByID(task1.getId()));
        System.out.println(manager.getSubtaskByID(subtask1.getId()));
        System.out.println(manager.getSubtaskByID(subtask2.getId()));
        System.out.println(manager.getSubtaskByID(subtask3.getId()));
        System.out.println(manager.getEpicByID(epic1.getId()));

        System.out.println();
        System.out.println(manager.getHistory());

        manager.removeTask(task1.getId());
        System.out.println();

        System.out.println(manager.getHistory());
        System.out.println();

        manager.removeEpic(epic1.getId());
        System.out.println();
        System.out.println(manager.getHistory());
    }
}
