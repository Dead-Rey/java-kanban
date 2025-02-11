package main.java.controllers;

import main.java.controllers.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

   private final HashMap<Integer,Node> nodes = new HashMap<>();

   private Node head;
   private Node tail;

   @Override
 public void add(Task task) {
    if (nodes.containsKey(task.getId())) {
        Node delNode = nodes.get(task.getId());
        removeNode(delNode);
    }
    linkLast(task);
    nodes.put(task.getId(), tail);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        if (nodes.containsKey(id)) {
            Node nodeToRemove = nodes.get(id);
            removeNode(nodeToRemove);
            nodes.remove(id);
        }
    }

   @Override
   public void linkLast(Task task) {
       Node newNode = new Node(task);
       if (tail == null) {
           head = newNode;
           tail = newNode;
       } else {
           tail.next = newNode;
           newNode.prev = tail;
           tail = newNode;
       }
   }

   @Override
   public List<Task> getTasks() {
       List<Task> tasks = new ArrayList<>();
       Node current = head;
       while (current != null) {
           tasks.add(current.task);
           current = current.next;
       }
       return tasks;
   }

    @Override
    public void removeNode(Node node) {
        if (node == null) {
            return;
        }
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }
}
