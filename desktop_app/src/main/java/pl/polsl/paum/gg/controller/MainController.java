package pl.polsl.paum.gg.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;
import pl.polsl.paum.gg.bind.CsvUnmarshaller;
import pl.polsl.paum.gg.bind.impl.CsvUnmarshallerImpl;
import pl.polsl.paum.gg.exception.ConversionException;
import pl.polsl.paum.gg.model.PedometerCsv;

public class MainController {

	private final CsvUnmarshaller csvUnmarshaller = new CsvUnmarshallerImpl();
	private PedometerCsv pedometerCsv = null;

	@FXML
	private Window window;

	@FXML
	private VBox vboxMain;

	@FXML
	private void initialize() {
		// act as constructor
	}

	@FXML
	public void onMenuItemOpenCsvAction() {
		File sourceFile;
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Pedometer CSV File");
		fileChooser.setInitialFileName("data.csv");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("CSV files (*.csv)", "*.csv"));
		sourceFile = fileChooser.showOpenDialog(window);
		try {
			pedometerCsv = csvUnmarshaller.unmarshalCsvToPojo(new FileReader(sourceFile));
		} catch (FileNotFoundException e) {
			showWarning("Nie uda³o siê otworzyæ podanego pliku.");
		} catch (ConversionException e) {
			showWarning("Zawartoœæ wskazanego pliku jest uszkodzona i nie uda³o siê jej przetworzyæ.");
		}

	}

	private void showWarning(String textToDisplay) {
		Alert alert = new Alert(AlertType.WARNING, textToDisplay);
		alert.getDialogPane().setHeaderText("Wyst¹pi³ b³¹d");
		alert.showAndWait();
	}

}
