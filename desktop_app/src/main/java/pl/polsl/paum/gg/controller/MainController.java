package pl.polsl.paum.gg.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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
import pl.polsl.paum.gg.model.StepRecord;
import pl.polsl.paum.gg.repository.PedometerCsvRepository;
import pl.polsl.paum.gg.repository.impl.PedometerCsvRepositoryImpl;

public class MainController {

	private final static int HOUR_SEPARATOR = 2; // show chart point every HOUR_SEPARATOR (sum all entities within
													// HOUR_SEPARATOR duration)
	private final CsvUnmarshaller csvUnmarshaller = new CsvUnmarshallerImpl();
	private final PedometerCsvRepository repository = new PedometerCsvRepositoryImpl();

	@FXML
	private Window window;

	@FXML
	private VBox vboxMain;

	@FXML
	private AreaChart<String, Integer> areaChartSteps;

	@FXML
	private CategoryAxis categoryAxisUnit;

	@FXML
	private DatePicker datePickerStartingDay;

	@FXML
	private ComboBox<String> comboBoxUnit;

	@FXML
	private void initialize() {
		initializeComboBoxUnit();
		initializeDatePickerStartingDay();
	}

	private void initializeComboBoxUnit() {
		comboBoxUnit.getItems().add(0, "Dzieñ");
		comboBoxUnit.getItems().add(1, "Tydzieñ");
		comboBoxUnit.getItems().add(2, "Miesi¹c");
		comboBoxUnit.setValue("Dzieñ");
	}

	private void initializeDatePickerStartingDay() {
		DailyStepRecord dailyStepRecord = repository.findDailyStepRecordByIndex(0);
		if (dailyStepRecord != null) {
			datePickerStartingDay.setValue(dailyStepRecord.getDate());
		}
	}

	private void initializeAreaChartSteps() {
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
			showWarning("Nie uda³o siê otworzyæ podanego pliku.");
			return;
		} catch (ConversionException e) {
			showWarning("Zawartoœæ wskazanego pliku jest uszkodzona i nie uda³o siê jej przetworzyæ.");
			return;
		}

	}

	@FXML
	public void onButtonVisualizeClicked() {
		// generate chart
		if (repository.isEmpty()) {
			showWarning("Nie za³adowano pliku z danymi.");
			return;
		}
		if (datePickerStartingDay.getValue() == null) {
			showWarning("Nie sprecyzowano daty startowej.");
			return;
		}
		String unit = comboBoxUnit.getValue();
		areaChartSteps.setTitle(unit);
		if ("Dzieñ".equals(unit)) {
			categoryAxisUnit.setLabel("Godziny");
			XYChart.Series<String, Integer> series = new XYChart.Series<>();
			series.setName("Kroki w godzinie");
			DailyStepRecord dailyStepRecord = repository
					.findDailyStepRecordByLocalDate(datePickerStartingDay.getValue());
			if (dailyStepRecord == null) {
				showWarning("Nie znaleziono danych w podanym dniu.");
				return;
			}
			series.getData().setAll(generateHourPoints(Duration.ofHours(HOUR_SEPARATOR), dailyStepRecord));
			areaChartSteps.setAnimated(false);
			areaChartSteps.getData().clear();
			areaChartSteps.getData().add(series);
		} else if ("Tydzieñ".equals(unit)) {
			categoryAxisUnit.setLabel("Dni");
		} else {
			categoryAxisUnit.setLabel("Dni");
//			XYChart.Data<String, Integer> x = new XYChart.Data<>("1:00", steps);
		}

	}

	private List<XYChart.Data<String, Integer>> generateHourPoints(Duration durationPerPoint,
			DailyStepRecord dailyRecord) {
		// null safe
		List<XYChart.Data<String, Integer>> result = new LinkedList<>();

		Duration currentDuration = Duration.ofSeconds(0);
		int stepSum = 0;
		LocalTime previousLocalTime = dailyRecord.getStepRecordList().get(0).getTime();

		for (StepRecord stepRecord : dailyRecord.getStepRecordList()) {
			currentDuration = currentDuration.plus(Duration.between(previousLocalTime, stepRecord.getTime()));
			stepSum += stepRecord.getStepsAmount();
			if (currentDuration.compareTo(durationPerPoint) >= 0) {
				result.add(new XYChart.Data<String, Integer>(
						stepRecord.getTime().truncatedTo(ChronoUnit.MINUTES).toString(), stepSum));
				currentDuration = Duration.ofSeconds(0);
				stepSum = 0;
			}
			previousLocalTime = stepRecord.getTime();
		}
		if (!currentDuration.isZero()) {
			StepRecord stepRecord = dailyRecord.getStepRecordList().get(dailyRecord.getStepRecordList().size() - 1);
			result.add(new XYChart.Data<String, Integer>(
					stepRecord.getTime().truncatedTo(ChronoUnit.MINUTES).toString(), stepSum));
		}
		return result;
	}

	private void showWarning(String textToDisplay) {
		Alert alert = new Alert(AlertType.WARNING, textToDisplay);
		alert.getDialogPane().setHeaderText("Wyst¹pi³ b³¹d");
		alert.showAndWait();
	}

}
