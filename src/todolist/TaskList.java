/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by     * the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the      * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR       * PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with    * this program. If not, see <https://www.gnu.org/licenses/>.
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
 *
 * @author sanadell
 */
public class TaskList implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient ObservableList<Task> taskList;

    public TaskList() {
        this.taskList = FXCollections.observableArrayList();
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList.setAll(taskList);
    }

    public void addtask(Task task) {
        this.taskList.add(task);
    }

    public void addtask(String pDescription, int pPriority, LocalDate pDueDate, String pStatus, LocalDate pStartDate, LocalDate pEndDate) {
        Task task = new Task(pDescription, pPriority, pDueDate, pStatus, pStartDate, pEndDate);
        if (this.taskList != null) {
            this.taskList.add(task);

        } else {
            this.taskList = FXCollections.observableArrayList();
            this.taskList.add(task);
        }
    }

    public boolean checkUniquePriority(int pPriority) {
        for (Task task : this.taskList) {
            if (task.getPriority() == pPriority) {
                return false;
            }
        }
        return true;
    }

    public boolean checkUniqueDescription(String pDescription) {
        for (Task task : this.taskList) {
            if (task.getDescription().equals(pDescription)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        String result = "";
        for (Task task : this.taskList) {
            result += task.toString();
        }
        return result;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        for (Task task : taskList) {
            out.writeUTF(task.getDescription());
            out.writeInt(task.getPriority());
            out.writeUnshared(task.getDueDate());
            out.writeUTF(task.getStatus());
            out.writeUnshared(task.getStartDate());
            out.writeUnshared(task.getEndDate());
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        while (in.available() > 0) {
            addtask(in.readUTF(), in.readInt(), (LocalDate) in.readUnshared(), in.readUTF(), (LocalDate) in.readUnshared(), (LocalDate) in.readUnshared());
        }
    }
}
