/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package todolist;

import javafx.scene.text.Text;
import javafx.stage.Stage;
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
public class ToDoListTest {
    
    public ToDoListTest() {
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
     * Test of start method, of class ToDoList.
     */
    @Test
    public void testStart() throws Exception {
        System.out.println("start");
        Stage stage = null;
        ToDoList instance = new ToDoList();
        instance.start(stage);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of validateAddItem method, of class ToDoList.
     */
    @Test
    public void testValidateAddItem() throws Exception {
        System.out.println("validateAddItem");
        Task task = null;
        ToDoList instance = new ToDoList();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setErrorStatus method, of class ToDoList.
     */
    @Test
    public void testSetErrorStatus() {
        System.out.println("setErrorStatus");
        Text pStatus = null;
        String pMsg = "";
        ToDoList instance = new ToDoList();
        instance.setErrorStatus(pStatus, pMsg);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setSuccessStatus method, of class ToDoList.
     */
    @Test
    public void testSetSuccessStatus() {
        System.out.println("setSuccessStatus");
        Text pStatus = null;
        String pMsg = "";
        ToDoList instance = new ToDoList();
        instance.setSuccessStatus(pStatus, pMsg);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFilepath method, of class ToDoList.
     */
    @Test
    public void testGetFilepath() {
        System.out.println("getFilepath");
        String expResult = "";
        String result = ToDoList.getFilepath();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setFilepath method, of class ToDoList.
     */
    @Test
    public void testSetFilepath() {
        System.out.println("setFilepath");
        String filepath = "";
        ToDoList.setFilepath(filepath);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTaskList method, of class ToDoList.
     */
    @Test
    public void testGetTaskList() {
        System.out.println("getTaskList");
        TaskList expResult = null;
        TaskList result = ToDoList.getTaskList();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setTaskList method, of class ToDoList.
     */
    @Test
    public void testSetTaskList() {
        System.out.println("setTaskList");
        TaskList taskList = null;
        ToDoList.setTaskList(taskList);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of main method, of class ToDoList.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        ToDoList.main(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
