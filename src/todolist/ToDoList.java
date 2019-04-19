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

import java.awt.SplashScreen;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * This class contains the core logic used for adding, updating, displaying
 * tasks.
 * It contains the UI controller logic and business logic needed.
 *
 * @author Sandeep Nadella
 */
public class ToDoList extends Application {

    // Member variables
    private static String filepath;
    private static TaskList taskList;

    /**
     * The entry point for the view
     *
     * @param stage
     *
     * @throws Exception
     */
    @Override
    public void start(final Stage stage) throws Exception {

        TitledPane root = (TitledPane) FXMLLoader.load(getClass().getResource(StringConstants.APP_UI_FILE_NAME));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(StringConstants.APP_TITLE);
        stage.setResizable(false);
        stage.getIcons().add(new Image(getClass().getResourceAsStream(StringConstants.ICON_FILE_NAME)));
        SplashScreen splashScreen;
        splashScreen = SplashScreen.getSplashScreen();
        if (splashScreen != null) {
            splashScreen.close();
        }
        AnchorPane rootAnchorPane = (AnchorPane) root.getContent();
        TabPane optionsPane = (TabPane) rootAnchorPane.getChildren().get(0);
        Button saveBtn = (Button) rootAnchorPane.getChildren().get(1);
        Button restoreBtn = (Button) rootAnchorPane.getChildren().get(2);
        Button printBtn = (Button) rootAnchorPane.getChildren().get(3);
        Text statusTxt = (Text) rootAnchorPane.getChildren().get(4);
        statusTxt.setTextAlignment(TextAlignment.CENTER);
        statusTxt.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue observable, String oldValue, String newValue) {
                // Create a timer thread to clear the status after a cetain time
                if (!newValue.isEmpty()) {
                    Thread statusTimer = new Thread(() -> {
                        try {
                            Thread.sleep(10000);
                            clearStatusText(statusTxt);
                        } catch (InterruptedException ex) {
                            statusTxt.setFill(Color.RED);
                            statusTxt.setText(StringConstants.MSG_APPLICATION_ERROR);
                        }
                    });
                    statusTimer.start();
                }
            }
        });
        try {
            //Create and open default file for writing
            openFile();
            saveBtn.setOnAction((ActionEvent event) -> {
                Tab selectedTab = optionsPane.getSelectionModel().getSelectedItem();
                if (selectedTab.getId().equals(StringConstants.ID_TAB_ADD_ITEMS)) {
                    saveFile(taskList, statusTxt);
                } else if (selectedTab.getId().equals(StringConstants.ID_TAB_UPDATE_ITEMS)) {
                    ScrollPane scrollUpdateItems = (ScrollPane) ((AnchorPane) selectedTab.getContent()).getChildren().get(0);
                    AnchorPane updateItemsAnchorPane = (AnchorPane) scrollUpdateItems.getContent();
                    AnchorPane updateItemRefRow = (AnchorPane) updateItemsAnchorPane.getChildren().get(0);
                    updateItemsAnchorPane.getChildren().get(0).setVisible(false);
                    TaskList updatedTaskList = new TaskList();
                    for (int i = 1; i < updateItemsAnchorPane.getChildren().size(); i++) {
                        AnchorPane rowAP = (AnchorPane) updateItemsAnchorPane.getChildren().get(i);
                        String description = null;
                        int priority = 0;
                        LocalDate dueDate = null;
                        String status = null;
                        LocalDate startDate = null;
                        LocalDate endDate = null;
                        boolean addToList = true;
                        for (int j = 0; j < rowAP.getChildren().size(); j++) {
                            switch (j) {
                                case 0: {
                                    description = ((TextField) rowAP.getChildren().get(j)).getText();
                                    addToList = ((TextField) rowAP.getChildren().get(j)).isVisible();
                                    break;
                                }
                                case 1: {
                                    priority = Integer.parseInt(((TextField) rowAP.getChildren().get(j)).getText());
                                    break;
                                }
                                case 2: {
                                    dueDate = ((DatePicker) rowAP.getChildren().get(j)).getValue();
                                    break;
                                }
                                case 3: {
                                    status = (String) ((ChoiceBox) rowAP.getChildren().get(j)).getSelectionModel().getSelectedItem();
                                    break;
                                }
                                case 4: {
                                    startDate = ((DatePicker) rowAP.getChildren().get(j)).getValue();
                                    break;
                                }
                                case 5: {
                                    endDate = ((DatePicker) rowAP.getChildren().get(j)).getValue();
                                    break;
                                }
                            }
                        }
                        if (addToList) {
                            Task updatedTask = new Task(description, priority, dueDate, status, startDate, endDate);
                            try {
                                validateAddItem(updatedTask, updatedTaskList);
                                updatedTaskList.addtask(updatedTask);
                            } catch (Exception e) {
                                setErrorStatus(statusTxt, e.getMessage());
                                return;
                            }
                        }
                    }
                    taskList = updatedTaskList;
                    saveFile(taskList, statusTxt);
                }
            });

            restoreBtn.setOnAction((ActionEvent event) -> {
                String bkpLocation = filepath;
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle(StringConstants.SAVED_FILE_CHOOSER_TITLE);
                File restoreFile = fileChooser.showOpenDialog(stage);
                if (restoreFile != null) {
                    filepath = restoreFile.getAbsolutePath();
                } else {
                    return;
                }
                try {
                    openFile(restoreFile.getParent(), restoreFile.getName());
                } catch (IOException | ClassNotFoundException ex) {
                    setErrorStatus(statusTxt, StringConstants.MSG_ERROR_CANNOT_READ_FILE);
                    filepath = bkpLocation;
                    try {
                        // reopen the previous file for writing
                        reopenFile(bkpLocation);
                    } catch (IOException | ClassNotFoundException e) {
                        setErrorStatus(statusTxt, StringConstants.MSG_ERROR_CANNOT_READ_FILE_CONTACT_SUPPORT);
                    }
                }
                optionsPane.getSelectionModel().clearAndSelect(0);
                for (Tab tab : optionsPane.getTabs()) {
                    if (tab.getId().equals(StringConstants.ID_TAB_ADD_ITEMS)) {
                        AnchorPane tabAnchorpane = ((AnchorPane) tab.getContent());
                        TableView addItemTableView = (TableView) tabAnchorpane.getChildren().get(1);
                        addItemTableView.getItems().clear();
                        addItemTableView.setItems((ObservableList) taskList.getTaskList());
                        AnchorPane addItemAnchorPane = (AnchorPane) tabAnchorpane.getChildren().get(0);
                        TextField priority = ((TextField) ((TitledPane) addItemAnchorPane.getChildren().get(1)).getContent());
                        priority.setText("1");
                    }
                }
            });

            printBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {

                    for (Tab tab : optionsPane.getTabs()) {
                        if (tab.getId().equals(StringConstants.ID_TAB_ADD_ITEMS)) {
                            AnchorPane tabAnchorpane = ((AnchorPane) tab.getContent());
                            TableView addItemTableView = (TableView) tabAnchorpane.getChildren().get(1);
                            // Print the table view. Only the table view displayed will be printed.
                            TablePrinter.print(addItemTableView, null, null, stage);
                        }
                    }
                }
            });

            for (Tab tab : optionsPane.getTabs()) {
                if (tab.getId().equals(StringConstants.ID_TAB_ADD_ITEMS)) {
                    AnchorPane tabAnchorpane = ((AnchorPane) tab.getContent());
                    AnchorPane addItemAnchorPane = (AnchorPane) tabAnchorpane.getChildren().get(0);
                    TextField description = ((TextField) ((TitledPane) addItemAnchorPane.getChildren().get(0)).getContent());
                    TextField priority = ((TextField) ((TitledPane) addItemAnchorPane.getChildren().get(1)).getContent());
                    priority.setText("1");
                    priority.textProperty().addListener(new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                            if (!newValue.matches("\\d*")) {
                                priority.setText(newValue.replaceAll("[^\\d]", StringConstants.STRING_EMPTY));
                            }
                            if (newValue.equals("0")) {
                                priority.setText(StringConstants.STRING_EMPTY);
                            }
                        }
                    });
                    DatePicker dueDate = ((DatePicker) ((TitledPane) addItemAnchorPane.getChildren().get(2)).getContent());
                    ChoiceBox status = ((ChoiceBox) ((TitledPane) addItemAnchorPane.getChildren().get(3)).getContent());
                    status.getSelectionModel().selectFirst();
                    DatePicker startDate = ((DatePicker) ((TitledPane) addItemAnchorPane.getChildren().get(4)).getContent());
                    DatePicker endDate = ((DatePicker) ((TitledPane) addItemAnchorPane.getChildren().get(5)).getContent());
                    Button addBtn = (Button) addItemAnchorPane.getChildren().get(6);
                    addBtn.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            //Validate the fields entered by the user
                            try {
                                taskList.movePriority(Integer.parseInt((priority.getText().equals(StringConstants.STRING_EMPTY)) ? "0" : priority.getText()));
                                Task newTask = new Task(description.getText(), Integer.parseInt((priority.getText().equals(StringConstants.STRING_EMPTY)) ? "0" : priority.getText()), dueDate.getValue(), (String) status.getValue(), startDate.getValue(), endDate.getValue());
                                validateAddItem(newTask, taskList);
                                taskList.addtask(newTask);
                                setSuccessStatus(statusTxt, StringConstants.MSG_SUCCESS_TASK_ADDED);
                                description.clear();
                                priority.clear();
                                dueDate.getEditor().clear();
                                status.getSelectionModel().selectFirst();
                                startDate.getEditor().clear();
                                endDate.getEditor().clear();
                                priority.setText("1");
                            } catch (Exception e) {
                                String error;
                                if (e.getMessage().isEmpty()) {
                                    error = StringConstants.MSG_APPLICATION_ERROR;
                                } else {
                                    error = e.getMessage();
                                }
                                setErrorStatus(statusTxt, error);
                            }
                        }
                    });

                    TableView addItemTableView = (TableView) tabAnchorpane.getChildren().get(1);
                    int colmns = addItemTableView.getColumns().size();
                    for (int i = 0; i < colmns; i++) {
                        TableColumn tableCol = (TableColumn) addItemTableView.getColumns().get(i);
                        String property;
                        switch (i) {
                            case 0:
                                property = StringConstants.COL_DESCRIPTION;
                                break;
                            case 1:
                                property = StringConstants.COL_PRIORITY;
                                break;
                            case 2:
                                property = StringConstants.COL_DUE_DATE;
                                break;
                            case 3:
                                property = StringConstants.COL_STATUS;
                                break;
                            case 4:
                                property = StringConstants.COL_START_DATE;
                                break;
                            case 5:
                                property = StringConstants.COL_END_DATE;
                                break;
                            default:
                                throw new Exception(StringConstants.MSG_APPLICATION_ERROR_CONTACT_SUPPORT);
                        }
                        tableCol.setCellValueFactory(new PropertyValueFactory<>(property));
                    }
                    addItemTableView.setItems((ObservableList) taskList.getTaskList());

                    optionsPane.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) -> {

                        if (newValue.getId().equals(StringConstants.ID_TAB_ADD_ITEMS)) {
                            refreshTable(addItemTableView);
                        } else if (newValue.getId().equals(StringConstants.ID_TAB_UPDATE_ITEMS)) {
                            ScrollPane scrollUpdateItems = (ScrollPane) ((AnchorPane) newValue.getContent()).getChildren().get(0);
                            AnchorPane updateItemsAnchorPane = (AnchorPane) scrollUpdateItems.getContent();
                            AnchorPane updateItemRefRow = (AnchorPane) updateItemsAnchorPane.getChildren().get(0);
                            updateItemsAnchorPane.getChildren().get(0).setVisible(false);
                            int sz = updateItemsAnchorPane.getChildren().size();
                            updateItemsAnchorPane.getChildren().clear();
                            updateItemsAnchorPane.getChildren().add(updateItemRefRow);
                            for (int j = 0; j < taskList.getTaskList().size(); j++) {
                                Task task = taskList.getTaskList().get(j);
                                AnchorPane row = new AnchorPane();
                                row.setLayoutX(updateItemRefRow.getLayoutX());
                                row.setLayoutY(updateItemRefRow.getLayoutY() + j * 50);
                                for (int i = 0; i < updateItemRefRow.getChildren().size(); i++) {
                                    Node existingRefNode = updateItemRefRow.getChildren().get(i);
                                    switch (i) {
                                        case 0: {
                                            TextField rowDescription = new TextField(task.getDescription());
                                            rowDescription.setLayoutX(((TextField) existingRefNode).getLayoutX());
                                            rowDescription.setLayoutY(existingRefNode.getLayoutY());
                                            rowDescription.setPrefHeight(((TextField) existingRefNode).getPrefHeight());
                                            rowDescription.setPrefWidth(((TextField) existingRefNode).getWidth());
                                            row.getChildren().add(rowDescription);
                                            break;
                                        }
                                        case 1: {
                                            TextField rowPriority = new TextField();
                                            rowPriority.setText(String.valueOf(task.getPriority()));
                                            rowPriority.setLayoutX(existingRefNode.getLayoutX());
                                            rowPriority.setLayoutY(existingRefNode.getLayoutY());
                                            rowPriority.setPrefHeight(((TextField) existingRefNode).getPrefHeight());
                                            rowPriority.setPrefWidth(((TextField) existingRefNode).getWidth());
                                            row.getChildren().add(rowPriority);
                                            break;
                                        }
                                        case 2: {
                                            DatePicker rowDueDate = new DatePicker(task.getDueDate());
                                            rowDueDate.setLayoutX(existingRefNode.getLayoutX());
                                            rowDueDate.setLayoutY(existingRefNode.getLayoutY());
                                            rowDueDate.setPrefHeight(((DatePicker) existingRefNode).getPrefHeight());
                                            rowDueDate.setPrefWidth(((DatePicker) existingRefNode).getWidth());
                                            row.getChildren().add(rowDueDate);
                                            break;
                                        }
                                        case 3: {
                                            ChoiceBox rowStatus = new ChoiceBox(((ChoiceBox) existingRefNode).getItems());
                                            rowStatus.setValue(task.getStatus());
                                            rowStatus.setLayoutX(existingRefNode.getLayoutX());
                                            rowStatus.setLayoutY(existingRefNode.getLayoutY());
                                            rowStatus.setPrefHeight(((ChoiceBox) existingRefNode).getPrefHeight());
                                            rowStatus.setPrefWidth(((ChoiceBox) existingRefNode).getWidth());
                                            row.getChildren().add(rowStatus);
                                            break;
                                        }
                                        case 4: {
                                            DatePicker rowStartDate = new DatePicker(task.getStartDate());
                                            rowStartDate.setLayoutX(existingRefNode.getLayoutX());
                                            rowStartDate.setLayoutY(existingRefNode.getLayoutY());
                                            rowStartDate.setPrefHeight(((DatePicker) existingRefNode).getPrefHeight());
                                            rowStartDate.setPrefWidth(((DatePicker) existingRefNode).getWidth());
                                            row.getChildren().add(rowStartDate);
                                            break;
                                        }
                                        case 5: {
                                            DatePicker rowEndDate = new DatePicker(task.getEndDate());
                                            rowEndDate.setLayoutX(existingRefNode.getLayoutX());
                                            rowEndDate.setLayoutY(existingRefNode.getLayoutY());
                                            rowEndDate.setPrefHeight(((DatePicker) existingRefNode).getPrefHeight());
                                            rowEndDate.setPrefWidth(((DatePicker) existingRefNode).getWidth());
                                            row.getChildren().add(rowEndDate);
                                            break;
                                        }
                                        case 6: {
                                            Button rowDelete = new Button(((Button) existingRefNode).getText());
                                            rowDelete.setLayoutX(existingRefNode.getLayoutX());
                                            rowDelete.setLayoutY(existingRefNode.getLayoutY());
                                            rowDelete.setPrefHeight(((Button) existingRefNode).getPrefHeight());
                                            rowDelete.setPrefWidth(((Button) existingRefNode).getWidth());
                                            rowDelete.setPadding(((Button) existingRefNode).getInsets());
                                            rowDelete.setOnAction(new EventHandler<ActionEvent>() {
                                                @Override
                                                public void handle(ActionEvent event) {
                                                    if (rowDelete.getText().equals("X")) {
                                                        for (int i = 0; i < 6; i++) {
                                                            ((AnchorPane) rowDelete.getParent()).getChildren().get(i).setVisible(false);
                                                        }
                                                        rowDelete.setText("+");
                                                    } else {
                                                        rowDelete.setText("X");
                                                        for (int i = 0; i < 6; i++) {
                                                            ((AnchorPane) rowDelete.getParent()).getChildren().get(i).setVisible(true);
                                                        }
                                                    }

                                                }
                                            });
                                            row.getChildren().add(rowDelete);
                                        }
                                    }
                                }
                                updateItemsAnchorPane.getChildren().add(row);
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            setErrorStatus(statusTxt, StringConstants.MSG_APPLICATION_ERROR);
        }
        stage.show();
    }

    /**
     * Save the tasks list to the file when navigating across tabs
     *
     * @param pTaskList
     * @param pStatus
     */
    private void saveState(Object pTaskList, Text pStatus) {
        writeToFile(pTaskList, pStatus);
    }

    /**
     * Save the task list to a file and set the status
     *
     * @param pTaskList
     * @param pStatus
     */
    private void saveFile(Object pTaskList, Text pStatus) {
        writeToFile(pTaskList, pStatus);
        setSuccessStatus(pStatus, StringConstants.MSG_SUCCESS_SAVE + filepath);
    }

    /**
     * Refresh table view
     *
     * @param table
     */
    private static void refreshTable(TableView table) {
//        table.getItems().clear();
        table.setItems((ObservableList) taskList.getTaskList());
//        table.refresh();
    }

    /**
     * Validate the task when adding to the task list
     *
     * @param task
     *
     * @throws Exception
     */
    public void validateAddItem(Task task, TaskList pTaskList) throws Exception {
        List errors = new ArrayList<String>();
        if (task.getDescription().equals(StringConstants.STRING_EMPTY)) {
            errors.add(StringConstants.COL_NAME_DESCRIPTION);
        }
        if (task.getPriority() == 0) {
            errors.add(StringConstants.COL_NAME_PRIORITY);
        }
        if (task.getDueDate() == null) {
            errors.add(StringConstants.COL_NAME_DUE_DATE);
        }
        if (task.getStatus() == null) {
            errors.add(StringConstants.COL_NAME_STATUS);
        }
        if (!errors.isEmpty()) {
            throw new Exception(Arrays.toString(errors.toArray()) + " fields required");
        }
        if (task.getStatus().equals(StringConstants.STATUS_IN_PROGRESS) && (task.getStartDate() == null)) {
            throw new Exception(StringConstants.MSG_NO_START_DATE_PROVIDED_FOR_IN_PROGRESS);
        }
        if (task.getStatus().equals(StringConstants.STATUS_FINISHED) && (task.getStartDate() == null)) {
            throw new Exception(StringConstants.MSG_NO_START_DATE_PROVIDED_FOR_FINISHED);
        }
        if (task.getStatus().equals(StringConstants.STATUS_FINISHED) && (task.getEndDate() == null)) {
            throw new Exception(StringConstants.MSG_NO_END_DATE_PROVIDED_FOR_FINISHED);
        }
        if (!pTaskList.checkUniquePriority(task.getPriority())) {
            throw new Exception(StringConstants.MSG_PRIORITY_MUST_BE_UNIQUE);
        }
        if (!pTaskList.checkUniqueDescription(task.getDescription())) {
            throw new Exception(StringConstants.MSG_TASK_MUST_BE_UNIQUE);
        }
    }

    /**
     * Clear out the status text
     *
     * @param pStatus
     */
    private void clearStatusText(Text pStatus) {
        pStatus.setText(StringConstants.STRING_EMPTY);
        pStatus.setFill(Color.BLACK);
        pStatus.setTextAlignment(TextAlignment.CENTER);
        pStatus.setFont(Font.font(StringConstants.STATUS_FONT, StringConstants.STATUS_FONT_SIZE));
    }

    /**
     * Set the error status with the given message string
     *
     * @param pStatus
     * @param pMsg
     */
    public void setErrorStatus(Text pStatus, String pMsg) {
        pStatus.setText(StringConstants.STRING_EMPTY);
        pStatus.setFill(Color.RED);
        pStatus.setTextAlignment(TextAlignment.CENTER);
        pStatus.setFont(Font.font(StringConstants.STATUS_FONT, StringConstants.STATUS_FONT_SIZE));
        pStatus.setText(pMsg);
    }

    /**
     * Set the success status with the given message string
     *
     * @param pStatus
     * @param pMsg
     */
    public void setSuccessStatus(Text pStatus, String pMsg) {
        pStatus.setText(StringConstants.STRING_EMPTY);
        pStatus.setFill(Color.BLACK);
        pStatus.setTextAlignment(TextAlignment.CENTER);
        pStatus.setFont(Font.font(StringConstants.STATUS_FONT, StringConstants.STATUS_FONT_SIZE));
        pStatus.setText(pMsg);
    }

    /**
     * Write the task list to the file
     *
     * @param pTaskListObject
     * @param pStatus
     */
    private void writeToFile(Object pTaskListObject, Text pStatus) {

        try {
            FileOutputStream fileOut = new FileOutputStream(filepath);
            try (ObjectOutputStream taskListFileOutputStream = new ObjectOutputStream(fileOut)) {
                taskListFileOutputStream.writeObject(pTaskListObject);
            }
        } catch (IOException ex) {
            setErrorStatus(pStatus, StringConstants.MSG_APPLICATION_ERROR);
        }
    }

    /**
     * Open the default file for writing
     *
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void openFile() throws FileNotFoundException, IOException, ClassNotFoundException {
        String folder = System.getProperty(StringConstants.USER_HOME_PROPERTY) + StringConstants.APP_DATA_FOLDER;
        openFile(folder, StringConstants.DEFAULT_FILE_NAME);
    }

    /**
     * Reopen the file from the given backup location
     *
     * @param pBackupFileLoc
     *
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void reopenFile(String pBackupFileLoc) throws FileNotFoundException, IOException, ClassNotFoundException {
        File file = new File(pBackupFileLoc);
        openFile(file.getParent(), file.getName());
    }

    /**
     * Open the file from the folder location given
     *
     * @param pFolder
     * @param pFileName
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void openFile(String pFolder, String pFileName) throws IOException, ClassNotFoundException {
        FileInputStream fileInput;
        filepath = pFolder + "/" + pFileName;
        try {
            fileInput = new FileInputStream(new File(filepath));
        } catch (FileNotFoundException e) {
            File dir = new File(pFolder);
            dir.mkdirs();
            File taskListFile = new File(dir, StringConstants.DEFAULT_FILE_NAME);
            if (!taskListFile.exists()) {
                taskListFile.createNewFile();
            }
            fileInput = new FileInputStream(taskListFile);
            taskList = new TaskList();
            fileInput.close();
            return;
        }
        try {
            try (ObjectInputStream objectInput = new ObjectInputStream(fileInput)) {
                taskList = (TaskList) objectInput.readObject();
            }
        } catch (EOFException e) {
            taskList = new TaskList();
        }
        fileInput.close();
    }

    /**
     * Get the file path of the file being used for read/write
     *
     * @return
     */
    public static String getFilepath() {
        return filepath;
    }

    /**
     * Set the file path of the file being used for read/write
     *
     * @param filepath
     */
    public static void setFilepath(String filepath) {
        ToDoList.filepath = filepath;
    }

    /**
     * Get the task list containing the list of all tasks
     *
     * @return
     */
    public static TaskList getTaskList() {
        return taskList;
    }

    /**
     * Set the task list containing the list of all tasks
     *
     * @param taskList
     */
    public static void setTaskList(TaskList taskList) {
        ToDoList.taskList = taskList;
    }

    /**
     * The entry point of the program
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
