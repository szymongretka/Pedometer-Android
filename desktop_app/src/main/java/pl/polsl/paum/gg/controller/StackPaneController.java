package pl.polsl.paum.gg.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Files;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import pl.polsl.paum.gg.bind.impl.CsvUnmarshallerImpl;
import pl.polsl.paum.gg.exception.ConversionException;
import pl.polsl.paum.gg.model.PedometerCsv;
import javafx.stage.Window;

public class StackPaneController {
	
	private final CsvUnmarshallerImpl csvUnmarshaller = new CsvUnmarshallerImpl();
	private PedometerCsv pedometerCsv = null;

	@FXML
	private Window window;

	@FXML
	private StackPane stackPaneMain;

	
	@FXML
	private void initialize() {
		//act as constructor
	}

	@FXML
	public void onButtonCsvClicked(MouseEvent event) {
		File sourceFile;
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Pedometer CSV File");
		fileChooser.setInitialFileName("data.csv");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("CSV files (*.csv)", "*.csv"));
		sourceFile = fileChooser.showOpenDialog(window);
		try {
			pedometerCsv = csvUnmarshaller.unmarshalCsvToPojo(new FileReader(sourceFile));
		} catch (FileNotFoundException e) {
			showWarning("Nie uda�o si� otworzy� podanego pliku.");
		} catch (ConversionException e) {
			showWarning("Zawarto�� wskazanego pliku jest uszkodzona i nie uda�o si� jej przetworzy�.");
		}
		
	}
	
	private void showWarning(String textToDisplay) {
		Alert alert = new Alert(AlertType.WARNING, textToDisplay);
		alert.getDialogPane().setHeaderText("Wyst�pi� b��d");
		alert.showAndWait();
	}

}
