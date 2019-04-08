/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by    * the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the     * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR      * PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with   * this program. If not, see <https://www.gnu.org/licenses/>.
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
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
 *
 * @author sanadell
 */
public class ToDoList extends Application {

    private static String filepath;
    private static TaskList taskList;

    @Override
    public void start(final Stage stage) throws Exception {
        TitledPane root = (TitledPane) FXMLLoader.load(getClass().getResource(APP_UI_FILE_NAME));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(APP_TITLE);
        stage.setResizable(false);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
        SplashScreen mySplash;
        mySplash = SplashScreen.getSplashScreen();
        if (mySplash != null) {
            mySplash.close();
        }

        AnchorPane rootAnchorPane = (AnchorPane) root.getContent();
        final TabPane optionsPane = (TabPane) rootAnchorPane.getChildren().get(0);
        Button saveBtn = (Button) rootAnchorPane.getChildren().get(1);
        Button restoreBtn = (Button) rootAnchorPane.getChildren().get(2);
        Button printBtn = (Button) rootAnchorPane.getChildren().get(3);

        final Text statusTxt = (Text) rootAnchorPane.getChildren().get(4);
        statusTxt.setTextAlignment(TextAlignment.CENTER);

        statusTxt.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue observable, String oldValue, String newValue) {

                if (!newValue.isEmpty()) {
                    Thread statusTimer = new Thread(() -> {
                        try {
                            Thread.sleep(10000);
                            clearStatusText(statusTxt);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ToDoList.class.getName()).log(Level.SEVERE, null, ex);
                            statusTxt.setFill(Color.RED);
                            statusTxt.setText(MSG_APPLICATION_ERROR);
                        }
                    });
                    statusTimer.start();
                }
            }
        });
        try {
            openFile();

            saveBtn.setOnAction((ActionEvent event) -> {
                saveFile(taskList, statusTxt);
            });

            restoreBtn.setOnAction((ActionEvent event) -> {
                String bkpLocation = filepath;
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle(SAVED_FILE_CHOOSER_TITLE);
                File restoreFile = fileChooser.showOpenDialog(stage);
                if (restoreFile != null) {
                    filepath = restoreFile.getAbsolutePath();
                }
                try {
                    openFile(restoreFile.getParent(), restoreFile.getName());
                } catch (IOException | ClassNotFoundException ex) {
                    setErrorStatus(statusTxt, MSG_ERROR_CANNOT_READ_FILE);
                    filepath = bkpLocation;
                    try {
                        reopenFile(bkpLocation);
                    } catch (IOException | ClassNotFoundException e) {
                        setErrorStatus(statusTxt, MSG_ERROR_CANNOT_READ_FILE_CONTACT_SUPPORT);
                    }
                }
                for (Tab tab : optionsPane.getTabs()) {
                    if (tab.getId().equals(ID_TAB_ADD_ITEMS)) {
                        AnchorPane tabAnchorpane = ((AnchorPane) tab.getContent());
                        final TableView addItemTableView = (TableView) tabAnchorpane.getChildren().get(1);
                        addItemTableView.getItems().clear();
                        addItemTableView.setItems((ObservableList) taskList.getTaskList());
                    }
                }
            });

            printBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {

                    for (Tab tab : optionsPane.getTabs()) {
                        if (tab.getId().equals(ID_TAB_ADD_ITEMS)) {
                            AnchorPane tabAnchorpane = ((AnchorPane) tab.getContent());
                            final TableView addItemTableView = (TableView) tabAnchorpane.getChildren().get(1);
                            TablePrinter tablePrinter = new TablePrinter();
                            tablePrinter.print(addItemTableView, null, null, stage);
                        }
                    }
                }
            });

            for (Tab tab : optionsPane.getTabs()) {
                if (tab.getId().equals(ID_TAB_ADD_ITEMS)) {
                    AnchorPane tabAnchorpane = ((AnchorPane) tab.getContent());
                    AnchorPane addItemAnchorPane = (AnchorPane) tabAnchorpane.getChildren().get(0);
                    final TextField description = ((TextField) ((TitledPane) addItemAnchorPane.getChildren().get(0)).getContent());
                    final TextField priority = ((TextField) ((TitledPane) addItemAnchorPane.getChildren().get(1)).getContent());
                    priority.textProperty().addListener(new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                            if (!newValue.matches("\\d*")) {
                                priority.setText(newValue.replaceAll("[^\\d]", STRING_EMPTY));
                            }
                            if (newValue.equals("0")) {
                                priority.setText(STRING_EMPTY);
                            }
                        }
                    });
                    final DatePicker dueDate = ((DatePicker) ((TitledPane) addItemAnchorPane.getChildren().get(2)).getContent());
                    final ChoiceBox status = ((ChoiceBox) ((TitledPane) addItemAnchorPane.getChildren().get(3)).getContent());
                    status.getSelectionModel().selectFirst();
                    final DatePicker startDate = ((DatePicker) ((TitledPane) addItemAnchorPane.getChildren().get(4)).getContent());
                    final DatePicker endDate = ((DatePicker) ((TitledPane) addItemAnchorPane.getChildren().get(5)).getContent());
                    Button addBtn = (Button) addItemAnchorPane.getChildren().get(6);
                    addBtn.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            //validate the fields entered by the user
                            try {
                                Task newTask = new Task(description.getText(), Integer.parseInt((priority.getText().equals(STRING_EMPTY)) ? "0" : priority.getText()), dueDate.getValue(), (String) status.getValue(), startDate.getValue(), endDate.getValue());
                                validateAddItem(newTask);
                                taskList.addtask(newTask);
                                setSuccessStatus(statusTxt, MSG_SUCCESS_TASK_ADDED);
                                description.clear();
                                priority.clear();
                                dueDate.getEditor().clear();
                                status.getSelectionModel().selectFirst();
                                startDate.getEditor().clear();
                                endDate.getEditor().clear();
                            } catch (Exception e) {
                                String error;
                                if (e.getMessage().isEmpty()) {
                                    error = MSG_APPLICATION_ERROR;
                                } else {
                                    error = e.getMessage();
                                }
                                setErrorStatus(statusTxt, error);
                            }
                        }
                        private static final String MSG_SUCCESS_TASK_ADDED = "Success: Task added";
                    });

                    final TableView addItemTableView = (TableView) tabAnchorpane.getChildren().get(1);

                    int colmns = addItemTableView.getColumns().size();
                    for (int i = 0; i < colmns; i++) {
                        TableColumn tableCol = (TableColumn) addItemTableView.getColumns().get(i);
                        String property;
                        switch (i) {
                            case 0:
                                property = COL_DESCRIPTION;
                                break;
                            case 1:
                                property = COL_PRIORITY;
                                break;
                            case 2:
                                property = COL_DUE_DATE;
                                break;
                            case 3:
                                property = COL_STATUS;
                                break;
                            case 4:
                                property = COL_START_DATE;
                                break;
                            case 5:
                                property = COL_END_DATE;
                                break;
                            default:
                                throw new Exception(MSG_APPLICATION_ERROR_CONTACT_SUPPORT);
                        }
                        tableCol.setCellValueFactory(new PropertyValueFactory<>(property));
                    }
                    addItemTableView.setItems((ObservableList) taskList.getTaskList());

                    optionsPane.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) -> {
                        //Call Save Action
                        saveState(taskList, statusTxt);
                        if (newValue.getId().equals(ID_TAB_ADD_ITEMS)) {
                            refreshTable(addItemTableView);
                        }
                    });
                }
            }
        } catch (Exception e) {
            setErrorStatus(statusTxt, MSG_APPLICATION_ERROR);
        }

        stage.show();
    }
    private static final String COL_END_DATE = "endDate";
    private static final String COL_START_DATE = "startDate";
    private static final String COL_STATUS = "status";
    private static final String COL_DUE_DATE = "dueDate";
    private static final String COL_PRIORITY = "priority";
    private static final String COL_DESCRIPTION = "description";
    private static final String SAVED_FILE_CHOOSER_TITLE = "Open Saved File";
    private static final String APP_UI_FILE_NAME = "ToDoListUI.fxml";
    private static final String APP_TITLE = "To-Do List Unlimited 2019";
    private static final String MSG_APPLICATION_ERROR_CONTACT_SUPPORT = "Error: Application Error! Contact Support";
    private static final String ID_TAB_ADD_ITEMS = "tab-addItems";
    private static final String MSG_ERROR_CANNOT_READ_FILE_CONTACT_SUPPORT = "Error: Cannot read from file. Contact Support.";
    private static final String MSG_ERROR_CANNOT_READ_FILE = "Error: Cannot read from file";
    private static final String MSG_APPLICATION_ERROR = "Error: Application Error! Contact Support.";

    public void saveState(Object pTaskList, Text pStatus) {
        writeToFile(pTaskList, pStatus);
    }

    public void saveFile(Object pTaskList, Text pStatus) {
        writeToFile(pTaskList, pStatus);
        setSuccessStatus(pStatus, MSG_SUCCESS_SAVE + filepath);
    }
    private static final String MSG_SUCCESS_SAVE = "Success: Task list saved to ";

    public String addItem() {
        return MSG_SUCCESS_ITEM_ADDED;
    }
    private static final String MSG_SUCCESS_ITEM_ADDED = "Success: Item Added";

    public static void refreshTable(TableView table) {
        for (int i = 0; i < table.getColumns().size(); i++) {
            ((TableColumn) (table.getColumns().get(i))).setVisible(false);
            ((TableColumn) (table.getColumns().get(i))).setVisible(true);
        }
    }

    public void validateAddItem(Task task) throws Exception {
        List errors = new ArrayList<String>();
        if (task.getDescription().equals(STRING_EMPTY)) {
            errors.add(COL_NAME_DESCRIPTION);
        }
        if (task.getPriority() == 0) {
            errors.add(COL_NAME_PRIORITY);
        }
        if (task.getDueDate() == null) {
            errors.add(COL_NAME_DUE_DATE);
        }
        if (task.getStatus() == null) {
            errors.add(COL_NAME_STATUS);
        }
        if (!errors.isEmpty()) {
            throw new Exception(Arrays.toString(errors.toArray()) + " fields required");
        }
        if (task.getStatus().equals(STATUS_IN_PROGRESS) && (task.getStartDate() == null)) {
            throw new Exception(MSG_NO_START_DATE_PROVIDED_FOR_IN_PROGRESS);
        }
        if (task.getStatus().equals(STATUS_FINISHED) && (task.getStartDate() == null)) {
            throw new Exception(MSG_NO_START_DATE_PROVIDED_FOR_FINISHED);
        }
        if (task.getStatus().equals(STATUS_FINISHED) && (task.getEndDate() == null)) {
            throw new Exception(MSG_NO_END_DATE_PROVIDED_FOR_FINISHED);
        }
        if (!taskList.checkUniquePriority(task.getPriority())) {
            throw new Exception(MSG_PRIORITY_MUST_BE_UNIQUE);
        }
        if (!taskList.checkUniqueDescription(task.getDescription())) {
            throw new Exception(MSG_TASK_MUST_BE_UNIQUE);
        }
    }
    private static final String STRING_EMPTY = "";
    private static final String COL_NAME_STATUS = " Status ";
    private static final String COL_NAME_DUE_DATE = " Due Date ";
    private static final String COL_NAME_PRIORITY = " Priority ";
    private static final String COL_NAME_DESCRIPTION = " Description ";
    private static final String MSG_TASK_MUST_BE_UNIQUE = "Task must be unique";
    private static final String MSG_PRIORITY_MUST_BE_UNIQUE = "Priority must be unique";
    private static final String MSG_NO_END_DATE_PROVIDED_FOR_FINISHED = "No End Date provided for Finished task";
    private static final String MSG_NO_START_DATE_PROVIDED_FOR_FINISHED = "No Start Date provided for Finished task";
    private static final String MSG_NO_START_DATE_PROVIDED_FOR_IN_PROGRESS = "No Start Date provided for In Progress task";
    private static final String STATUS_FINISHED = "Finished";
    private static final String STATUS_IN_PROGRESS = "In Progress";

    public void clearStatusText(Text pStatus) {
        pStatus.setText(STRING_EMPTY);
        pStatus.setFill(Color.BLACK);
        pStatus.setTextAlignment(TextAlignment.CENTER);
        pStatus.setFont(Font.font(STATUS_FONT, STATUS_FONT_SIZE));
    }

    public void setErrorStatus(Text pStatus, String pMsg) {
        pStatus.setText(STRING_EMPTY);
        pStatus.setFill(Color.RED);
        pStatus.setTextAlignment(TextAlignment.CENTER);
        pStatus.setFont(Font.font(STATUS_FONT, STATUS_FONT_SIZE));
        pStatus.setText(pMsg);
    }
    private static final int STATUS_FONT_SIZE = 18;
    private static final String STATUS_FONT = "Verdana";

    public void setSuccessStatus(Text pStatus, String pMsg) {
        pStatus.setText(STRING_EMPTY);
        pStatus.setFill(Color.BLACK);
        pStatus.setTextAlignment(TextAlignment.CENTER);
        pStatus.setFont(Font.font(STATUS_FONT, STATUS_FONT_SIZE));
        pStatus.setText(pMsg);
    }

    public void writeToFile(Object pTaskListObject, Text pStatus) {

        try {
            FileOutputStream fileOut = new FileOutputStream(filepath);
            try (ObjectOutputStream taskListFileOutputStream = new ObjectOutputStream(fileOut)) {
                taskListFileOutputStream.writeObject(pTaskListObject);
            }
        } catch (IOException ex) {
            setErrorStatus(pStatus, MSG_APPLICATION_ERROR);
        }
    }

    public void openFile() throws FileNotFoundException, IOException, ClassNotFoundException {
        String folder = System.getProperty(USER_HOME_PROPERTY) + APP_DATA_FOLDER;
        openFile(folder, DEFAULT_FILE_NAME);
    }
    private static final String USER_HOME_PROPERTY = "user.home";
    private static final String DEFAULT_FILE_NAME = "defaultList.tdl";
    private static final String APP_DATA_FOLDER = "/ToDoListUnlimitedAppData/";

    public void reopenFile(String pBackupFileLoc) throws FileNotFoundException, IOException, ClassNotFoundException {
        File file = new File(pBackupFileLoc);
        openFile(file.getParent(), file.getName());
    }

    public void openFile(String pFolder, String pFileName) throws IOException, ClassNotFoundException {
        FileInputStream fileInput;
        filepath = pFolder + "/" + pFileName;
        try {
            fileInput = new FileInputStream(new File(filepath));
        } catch (FileNotFoundException e) {
            File dir = new File(pFolder);
            dir.mkdirs();
            File taskListFile = new File(dir, DEFAULT_FILE_NAME);
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
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
