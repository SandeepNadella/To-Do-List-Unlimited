/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by    
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the     
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR      
 * PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with   
 * this program. If not, see <https://www.gnu.org/licenses/>.
 */
package todolist;

import java.io.Serializable;
import java.time.LocalDate;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This class describes a task bean
 * 
 * @author Sandeep Nadella
 */
public class Task implements Serializable {
    
    private static final long serialVersionUID = 2L;

    // Member variables
    private transient StringProperty description;
    private transient IntegerProperty priority;
    private transient ObjectProperty<LocalDate> dueDate;
    private transient StringProperty status;
    private transient ObjectProperty<LocalDate> startDate;
    private transient ObjectProperty<LocalDate> endDate;

    /**
     * Create a task with the given input parameters
     * 
     * @param description
     * @param priority
     * @param dueDate
     * @param status
     * @param startDate
     * @param endDate 
     */
    public Task(String description, int priority, LocalDate dueDate, String status, LocalDate startDate, LocalDate endDate) {
        this.description = new SimpleStringProperty(description);
        this.priority = new SimpleIntegerProperty(priority);
        this.dueDate = new SimpleObjectProperty<>(dueDate);
        this.status = new SimpleStringProperty(status);
        this.startDate = new SimpleObjectProperty<>(startDate);
        this.endDate = new SimpleObjectProperty<>(endDate);
    }

    /**
     * Create a task with the given input parameters
     * 
     * @param description
     * @param priority
     * @param dueDate
     * @param status 
     */
    public Task(String description, int priority, LocalDate dueDate, String status) {
        this.description = new SimpleStringProperty(description);
        this.priority = new SimpleIntegerProperty(priority);
        this.dueDate = new SimpleObjectProperty<>(dueDate);
        this.status = new SimpleStringProperty(status);
    }

    /**
     * Create a task with the given input parameters
     * 
     * @param description
     * @param priority
     * @param dueDate
     * @param status
     * @param startDate 
     */
    public Task(String description, int priority, LocalDate dueDate, String status, LocalDate startDate) {
        this.description = new SimpleStringProperty(description);
        this.priority = new SimpleIntegerProperty(priority);
        this.dueDate = new SimpleObjectProperty<>(dueDate);
        this.status = new SimpleStringProperty(status);
        this.startDate = new SimpleObjectProperty<>(startDate);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public int getPriority() {
        return priority.get();
    }

    public void setPriority(int priority) {
        this.priority.set(priority);
    }

    public LocalDate getDueDate() {
        return dueDate.get();
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate.set(dueDate);
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public LocalDate getStartDate() {
        return startDate.get();
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate.set(startDate);
    }

    public LocalDate getEndDate() {
        return endDate.get();
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate.set(endDate);
    }
    
    /**
     * Get a formatted string representation of a task
     * 
     * @return 
     */
    @Override
    public String toString() {
      String result  = "";
      result += (!description.get().isEmpty())?description.get():"";
      result += ",";
      result += (priority.get() != 0)?String.valueOf(priority.get()):"";
      result += ",";
      result += (dueDate.get() != null)?dueDate.get():"";
      result += ",";
      result += (!status.get().isEmpty())?status.get():"";
      result += ",";
      result += (startDate.get() != null)?startDate.get():"";
      result += ",";
      result += (endDate.get() != null)?endDate.get():"";
      result += "\r\n";
      
      return result;
    }
}
