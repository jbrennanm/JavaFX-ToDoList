package csc335.ToDoList;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * JavaFX ToDoListGUI.java author: Brennan Mitchell
 */
public class ToDoListGUI extends Application {

	BorderPane pane = new BorderPane();
	BorderPane buttons = new BorderPane();
	BorderPane addDeleteButtons = new BorderPane();
	BorderPane priorityChange = new BorderPane();
	TextField typeToDo = new TextField("Delete This and Input To-Do");
	Button add = new Button("Add To-Do");
	Button delete = new Button("Delete To-Do");
	Button raise = new Button("Raise Priority");
	Button lower = new Button("Lower Priority");
	BorderPane confirmation = new BorderPane();
	Button confirmAdd = new Button("Confirm");
	Button cancelAdd = new Button("Cancel");
	String file = "myTODOs.ser";
	Label label = new Label("Use Commas to Separate To-Do List Items");
	ObservableList<String> toDos;
	ListView<String> listView;

	@Override
	public void start(Stage stage) {
		getToDoList();
		registerHandlers();
		addDeleteButtons.setLeft(add);
		addDeleteButtons.setRight(delete);
		priorityChange.setLeft(raise);
		priorityChange.setRight(lower);
		buttons.setLeft(addDeleteButtons);
		buttons.setRight(priorityChange);
		pane.setCenter(listView);
		pane.setBottom(buttons);
		var javaVersion = SystemInfo.javaVersion();
		var javafxVersion = SystemInfo.javafxVersion();
		var scene = new Scene(pane, 640, 480);
		stage.setOnCloseRequest((event) -> {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setHeaderText("Click cancel to discard changes");
			alert.setContentText("Click OK to save current To-Do List");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				saveList();
				Platform.exit();
				System.exit(0);
			} else {
				Platform.exit();
				System.exit(0);
			}
		});
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch();
	}

	private void registerHandlers() {
		add.setOnAction((event) -> {
			confirmation.setTop(typeToDo);
			confirmation.setLeft(confirmAdd);
			confirmation.setRight(cancelAdd);
			pane.setTop(confirmation);
		});
		confirmAdd.setOnAction((event) -> {
			String toAdd = typeToDo.getText();
			toDos.add(0, toAdd);
			listView.getSelectionModel().select(0);
			typeToDo.setText("Delete This and Input To-Do");
			pane.setTop(null);
		});
		cancelAdd.setOnAction((event) -> {
			pane.setTop(null);
			typeToDo.setText("Delete This and Input To-Do");
		});
		delete.setOnAction((event) -> {
			int index = listView.getSelectionModel().getSelectedIndex();
			if (index >= 0)
				toDos.remove(index);
		});
		raise.setOnAction((event) -> {
			int index = listView.getSelectionModel().getSelectedIndex();
			if (index > 0) {
				String raise = toDos.get(index);
				toDos.remove(index);
				toDos.add(index - 1, raise);
				listView.getSelectionModel().select(index - 1);
			}
		});
		lower.setOnAction((event) -> {
			int index = listView.getSelectionModel().getSelectedIndex();
			if (index < toDos.size() - 1 && index > -1) {
				String lower = toDos.get(index);
				toDos.remove(index);
				toDos.add(index + 1, lower);
				listView.getSelectionModel().select(index + 1);
			}
		});
	}

	private void getToDoList() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setHeaderText("Click cancel to start with an empty list");
		alert.setContentText("Click OK to read from a .ser file");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			loadList();
		} else {
			toDos = FXCollections.observableArrayList(new ArrayList<String>());
			listView = new ListView<String>(toDos);
		}
	}

	private void saveList() {
		ArrayList<String> toArray = new ArrayList<String>();
		for (int i = 0; i < toDos.size(); i++) {
			toArray.add(i, toDos.get(i));
		}
		try {
			FileOutputStream bytesToDisk = new FileOutputStream(file);
			ObjectOutputStream outFile = new ObjectOutputStream(bytesToDisk);
			// outFile understands the writeObject message. Make the objects persist so they
			// can be read later.
			outFile.writeObject(toArray);
			outFile.close(); // Always close the output file!
		} catch (IOException ioe) {
			System.out.println("Writing objects failed");
		}
	}

	private void loadList() {
		FileInputStream rawBytes;
		try {
			rawBytes = new FileInputStream(this.file); // Read the .ser file just created
			ObjectInputStream inFile = new ObjectInputStream(rawBytes);
			// Read the entire object from the file on disk. Casts required
			ArrayList<String> first = (ArrayList<String>) inFile.readObject();
			// Should close input files also
			inFile.close();
			toDos = FXCollections.observableArrayList(first);
			listView = new ListView<String>(toDos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}