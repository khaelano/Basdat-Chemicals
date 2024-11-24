package org.basdat.basdatchemicals;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class BasdatChemicals extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-page.fxml"));
        HBox mainPage = fxmlLoader.load();
        Scene scene = new Scene(mainPage, 1000, 800);
        stage.setTitle("Basdat Chemicals");
        stage.setScene(scene);
        stage.show();

        BasdatChemicalsDB db = new BasdatChemicalsDB("localhost", 1433, "Dataset_Kosmetik", "SA", "Anjay@mssql123");
        MainPage mainController = fxmlLoader.getController();
        mainController.setDbConnection(db);
    }

    public static void main(String[] args) {
        launch();
    }
}