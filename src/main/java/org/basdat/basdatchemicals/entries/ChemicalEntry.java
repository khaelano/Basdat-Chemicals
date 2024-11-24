package org.basdat.basdatchemicals.entries;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.basdat.basdatchemicals.BasdatChemicalsDB;
import org.basdat.basdatchemicals.entitities.Chemical;
import org.basdat.basdatchemicals.popups.ChemicalPopup;

import java.io.IOException;
import java.sql.SQLException;

public class ChemicalEntryController {
    private BasdatChemicalsDB dbConnection;
    private Chemical chemical;

    public Button moreInformationButton;
    public TitledPane chemEntry;
    public Text chemName;
    public Text chemCasNum;

    public void insertData(Chemical chemical) {
        this.chemEntry.setText(chemical.chemicalName() + " (Chemical)");
        this.chemName.setText(chemical.chemicalName());
        this.chemCasNum.setText(chemical.casNumber());
        this.chemical = chemical;
    }

    @FXML
    protected void getMoreInformation() throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/basdat/basdatchemicals/popups/chemical-popup.fxml"));
        AnchorPane popupWidget = loader.load();
        ChemicalPopup controller = loader.getController();
        controller.setDbConnection(dbConnection);
        controller.insertData(chemical);

        Scene popupScene = new Scene(popupWidget, 800, 600);
        Stage popupStage = new Stage();
        popupStage.setScene(popupScene);
        popupStage.setResizable(false);
        popupStage.show();
    }

    public void setDbConnection(BasdatChemicalsDB connection) {
        this.dbConnection = connection;
    }
}
