package pl.polsl.paum.gg.view;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class PedometerAnalyzerApplication extends Application {

	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/StackPaneWindow.fxml"));
		StackPane stackPane = loader.load();

		Scene scene = new Scene(stackPane);
		stage.setScene(scene);
		stage.setTitle("PedometerAnalyzer");
		stage.show();
	}

	public void run(String[] args) {
		launch(args);
	}

}
