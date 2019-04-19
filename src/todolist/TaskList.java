/* 
 * Copyright (C) 2019 Sandeep Nadella <vnadell1@asu.edu>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package todolist;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This class describes the task list bean
 *
 * @author Sandeep Nadella
 */
public class TaskList implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient ObservableList<Task> taskList;

    /**
     * Create a new task list
     */
    public TaskList() {
        this.taskList = FXCollections.observableArrayList();
    }

    /**
     * Get the task list
     *
     * @return
     */
    public List<Task> getTaskList() {
        return taskList;
    }

    /**
     * Set the task list
     *
     * @param taskList
     */
    public void setTaskList(List<Task> taskList) {
        this.taskList.setAll(taskList);
    }

    /**
     * Add the task to the task list
     *
     * @param task
     */
    public void addtask(Task task) {
        this.taskList.add(task);
    }

    /**
     * Add task with the all the task properties passed
     *
     * @param pDescription
     * @param pPriority
     * @param pDueDate
     * @param pStatus
     * @param pStartDate
     * @param pEndDate
     */
    public void addtask(String pDescription, int pPriority, LocalDate pDueDate, String pStatus, LocalDate pStartDate, LocalDate pEndDate) {
        Task task = new Task(pDescription, pPriority, pDueDate, pStatus, pStartDate, pEndDate);
        if (this.taskList != null) {
            this.taskList.add(task);

        } else {
            this.taskList = FXCollections.observableArrayList();
            this.taskList.add(task);
        }
    }

    /**
     * Check if the priority passed is unique across all tasks in task list
     *
     * @param pPriority
     *
     * @return
     */
    public boolean checkUniquePriority(int pPriority) {
        for (Task task : this.taskList) {
            if (task.getPriority() == pPriority) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the description passed is unique across all tasks in task list
     *
     * @param pDescription
     *
     * @return
     */
    public boolean checkUniqueDescription(String pDescription) {
        for (Task task : this.taskList) {
            if (task.getDescription().equals(pDescription)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Move the priorities of all the tasks above the given priority by one to
     * accommodate the new task
     *
     * @param pPriority
     */
    public void movePriority(int pPriority) {
        for (Task task : taskList) {
            if (task.getPriority() >= pPriority) {
                task.setPriority(task.getPriority() + 1);
            }
        }
    }

    /**
     * Get the string representation of the task list
     *
     * @return
     */
    @Override
    public String toString() {
        String result = "";
        for (Task task : this.taskList) {
            result += task.toString();
        }
        return result;
    }

    /**
     * Writes all the tasks in the task list to the given output stream
     *
     * @param out
     *
     * @throws IOException
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        if (taskList != null) {
            for (Task task : taskList) {
                out.writeUTF(task.getDescription());
                out.writeInt(task.getPriority());
                out.writeUnshared(task.getDueDate());
                out.writeUTF(task.getStatus());
                out.writeUnshared(task.getStartDate());
                out.writeUnshared(task.getEndDate());
            }
        }
    }

    /**
     * Reads from the input stream and adds to the task list
     *
     * @param in
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        while (in.available() > 0) {
            addtask(in.readUTF(), in.readInt(), (LocalDate) in.readUnshared(), in.readUTF(), (LocalDate) in.readUnshared(), (LocalDate) in.readUnshared());
        }
    }
}
