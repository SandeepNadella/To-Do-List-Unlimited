/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package todolist;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author sanadell
 */
public class TaskListTest {
    
    public TaskListTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of getTaskList method, of class TaskList.
     */
    @Test
    public void testGetTaskList() {
        System.out.println("getTaskList");
        TaskList instance = new TaskList();
        List<Task> expResult = null;
        List<Task> result = instance.getTaskList();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setTaskList method, of class TaskList.
     */
    @Test
    public void testSetTaskList() {
        System.out.println("setTaskList");
        List<Task> taskList = null;
        TaskList instance = new TaskList();
        instance.setTaskList(taskList);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addtask method, of class TaskList.
     */
    @Test
    public void testAddtask_Task() {
        System.out.println("addtask");
        Task task = null;
        TaskList instance = new TaskList();
        instance.addtask(task);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addtask method, of class TaskList.
     */
    @Test
    public void testAddtask_6args() {
        System.out.println("addtask");
        String pDescription = "";
        int pPriority = 0;
        LocalDate pDueDate = null;
        String pStatus = "";
        LocalDate pStartDate = null;
        LocalDate pEndDate = null;
        TaskList instance = new TaskList();
        instance.addtask(pDescription, pPriority, pDueDate, pStatus, pStartDate, pEndDate);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of checkUniquePriority method, of class TaskList.
     */
    @Test
    public void testCheckUniquePriority() {
        System.out.println("checkUniquePriority");
        int pPriority = 0;
        TaskList instance = new TaskList();
        boolean expResult = false;
        boolean result = instance.checkUniquePriority(pPriority);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of checkUniqueDescription method, of class TaskList.
     */
    @Test
    public void testCheckUniqueDescription() {
        System.out.println("checkUniqueDescription");
        String pDescription = "";
        TaskList instance = new TaskList();
        boolean expResult = false;
        boolean result = instance.checkUniqueDescription(pDescription);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class TaskList.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        TaskList instance = new TaskList();
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
