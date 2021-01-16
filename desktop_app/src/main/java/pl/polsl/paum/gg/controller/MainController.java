package pl.polsl.paum.gg.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;
import pl.polsl.paum.gg.bind.CsvUnmarshaller;
import pl.polsl.paum.gg.bind.impl.CsvUnmarshallerImpl;
import pl.polsl.paum.gg.exception.ConversionException;
import pl.polsl.paum.gg.model.DailyStepRecord;
import pl.polsl.paum.gg.model.PedometerCsv;
import pl.polsl.paum.gg.model.StepRecord;
import pl.polsl.paum.gg.repository.PedometerCsvRepository;
import pl.polsl.paum.gg.repository.impl.PedometerCsvRepositoryImpl;

public class MainController {

	private final CsvUnmarshaller csvUnmarshaller = new CsvUnmarshallerImpl();
	private final PedometerCsvRepository repository = new PedometerCsvRepositoryImpl();

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
		if (sourceFile == null) {
			return;
		}
		try {
			repository.setSource(csvUnmarshaller.unmarshalCsvToPojo(new FileReader(sourceFile)));
			initializeDatePickerStartingDay();
		} catch (FileNotFoundException e) {
			showWarning("Nie uda�o si� otworzy� podanego pliku.");
			return;
		} catch (ConversionException e) {
			showWarning("Zawarto�� wskazanego pliku jest uszkodzona i nie uda�o si� jej przetworzy�.");
			return;
		}

	}
	
	@FXML
	public void onButtonVisualizeClicked() {
		
	}
	

	private void showWarning(String textToDisplay) {
		Alert alert = new Alert(AlertType.WARNING, textToDisplay);
		alert.getDialogPane().setHeaderText("Wyst�pi� b��d");
		alert.showAndWait();
	}

}
