/*
 * Source: https://jluger.de/blog/20160915_javafx_printing_textview_content.html
 * JavaFX doesn't have native support for printing TableView. So using this guys work.
 */
package todolist;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/**
 * This class provides a method to print the content of a {@link TableView}. It
 * is at an early stage and currently doesn't support advanced CellFactorys.
 */
public class TablePrinter {

    /**
     * Prints the content of the provided {@link TableView}.
     *
     * @param <T>
     * @param tableView
     *                   See description.
     * @param jobArg
     *                   The {@link PrinterJob} to use. When the value is
     *                   <code>null</code> this method creates a default
     *                   {@link PrinterJob} and ends it. When a valid
     *                   {@link PrinterJob} is provided, the caller must close it.
     * @param pageLayout
     * @param stage
     *
     * @return
     */
    public static <T> boolean print(TableView<T> tableView, PrinterJob jobArg, PageLayout pageLayout, Stage stage) {
        boolean createJob = jobArg == null;
        PrinterJob job;
        if (createJob) {
            job = PrinterJob.createPrinterJob();
        } else {
            job = jobArg;
        }
        job.showPrintDialog(stage.getOwner());
        printWithJob(tableView, job, pageLayout);
        if (createJob) {
            job.endJob();
        }
        return true;
    }

    /**
     * The entry method for printing the table contents where {@link PrinterJob}
     * is guaranteed to not be <code>null</code>.
     *
     * @param tableView
     *                  See description.
     * @param job
     *                  See description.
     */
    private static <T> void printWithJob(TableView<T> tableView, PrinterJob job, PageLayout pageLayout) {
        TableView<T> copyView = createTableCopy(tableView, job);
        ArrayList<T> itemList = new ArrayList<>(tableView.getItems());
        while (itemList.size() > 0) {
            printPage(job, copyView, itemList, pageLayout);
        }
    }

    /**
     * Prints the first n-th items of the list with the help of the provided
     * {@link TableView}. The concrete number of items that will be printed is
     * the maximum of items that you can add to the table so that the vertical
     * scrollbar of the table will not be visible.<br>
     * All printed items are removed of the given list.
     *
     * @param job
     *                 The job used for printing.
     * @param copyView
     *                 See description.
     * @param itemList
     *                 See description.
     */
    private static <T> void printPage(PrinterJob job, TableView<T> copyView, ArrayList<T> itemList, PageLayout pageLayout) {
        ScrollBar verticalScrollbar = getVerticalScrollbar(copyView);
        ObservableList<T> batchItemList = FXCollections.observableArrayList();
        copyView.setItems(batchItemList);
        batchItemList.add(itemList.remove(0));
        while (!verticalScrollbar.isVisible() && itemList.size() > 0) {
            T item = itemList.remove(0);
            batchItemList.add(item);
            copyView.layout();
        }
        if (batchItemList.size() > 1 && verticalScrollbar.isVisible()) {
            T item = batchItemList.remove(batchItemList.size() - 1);
            itemList.add(0, item);
            copyView.layout();
        }
        if (pageLayout != null) {
            job.printPage(pageLayout, copyView);
        } else {
            job.printPage(copyView);
        }
    }

    /**
     * Create a new {@link TableView} that copies several settings from the
     * given one but uses the width/height based on the settings of the print
     * job.
     *
     * @param tableView
     *                  See description.
     * @param job
     *                  See description.
     *
     * @return See description.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <T> TableView<T> createTableCopy(TableView<T> tableView, PrinterJob job) {
        TableView<T> copyView = new TableView<>();
        Printer printer = Printer.getDefaultPrinter();
        PageLayout layout = printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, Printer.MarginType.DEFAULT);
        job.getJobSettings().setPageLayout(layout);
        PageLayout pageLayout = job.getJobSettings().getPageLayout();
        Paper paper = pageLayout.getPaper();
        double paperHeight = paper.getHeight() - pageLayout.getTopMargin() - pageLayout.getBottomMargin();
        double paperWidth = paper.getWidth() - pageLayout.getLeftMargin() - pageLayout.getRightMargin();
        if (pageLayout.getPageOrientation().equals(PageOrientation.PORTRAIT)) {
            copyView.setPrefHeight(paperHeight);
            copyView.setPrefWidth(paperWidth);
        } else {
            copyView.setPrefHeight(paperWidth);
            copyView.setPrefWidth(paperHeight);
        }
        copyView.setColumnResizePolicy(tableView.getColumnResizePolicy());
        double columnWidth = 0;
        for(TableColumn t : tableView.getColumns()) {
            if(t.isVisible()){
            columnWidth += t.getPrefWidth();
            }
        }
        for(TableColumn t : tableView.getColumns()) {
            if(t.isVisible()){
            TableColumn cloneColumn = new TableColumn(t.getText());
            cloneColumn.setMaxWidth(t.getMaxWidth());
            cloneColumn.setPrefWidth((paperHeight-40)*(t.getPrefWidth()/columnWidth));
            if (t.getCellValueFactory() != null) {
                cloneColumn.setCellValueFactory(t.getCellValueFactory());
            }
            if (t.getCellFactory() != null) {
                cloneColumn.setCellFactory(t.getCellFactory());
            }
             copyView.getColumns().add(cloneColumn);
            }
        }
        new Scene(copyView);
        copyView.getScene().getStylesheets().add(TablePrinter.class.getResource("TablePrint.css").toString());
        return copyView;
    }

    /**
     * Searches the vertical scrollbar in the {@link TableView}. The scrollbar
     * won't be available on a off screen {@link TableView} (one that was never
     * added to a visible stage) until at least once the snapshot method was
     * called. The snapshot method is somehow expensive thus it can't be called
     * too often. Thus this entry method is needed.
     *
     * @param tableView
     *                  See description.
     *
     * @return The found {@link ScrollBar} or <code>null</code>, when none was
     *         found.
     */
    private static <T> ScrollBar getVerticalScrollbar(TableView<T> tableView) {
        tableView.snapshot(new SnapshotParameters(), null);
        return getVerticalScrollbarForParent(tableView);
    }

    /**
     * Searches for {@link ScrollBar} in the given {@link Parent} but stops when
     * the node is {@link TableCell}
     *
     * @param p
     *          See description.
     *
     * @return The found {@link ScrollBar} or <code>null</code>, when none was
     *         found.
     */
    private static ScrollBar getVerticalScrollbarForParent(Parent p) {
        for (Node n : p.getChildrenUnmodifiable()) {
            if (n instanceof ScrollBar) {
                ScrollBar s = (ScrollBar) n;
                if (s.getOrientation() == Orientation.VERTICAL) {
                    return s;
                }
            }
            if (n instanceof Parent && !(p instanceof TableCell)) {
                ScrollBar scrollbar = getVerticalScrollbarForParent((Parent) n);
                if (scrollbar != null) {
                    return scrollbar;
                }
            }
        }
        return null;
    }
}
