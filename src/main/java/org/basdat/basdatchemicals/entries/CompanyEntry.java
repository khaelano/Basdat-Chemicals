package org.basdat.basdatchemicals.entries;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.basdat.basdatchemicals.BasdatChemicalsDB;
import org.basdat.basdatchemicals.entitities.Company;
import org.basdat.basdatchemicals.popups.CompanyPopup;

import java.io.IOException;
import java.sql.SQLException;

public class CompanyEntryController {
    private BasdatChemicalsDB dbConnection;
    private Company company;

    public TitledPane compEntry;
    public Text companyName;
    public Text brandCount;
    public Text productCount;

    public void insertData(Company company) {
        this.company = company;
        this.compEntry.setText(company.companyName() + " (Company)");
        this.companyName.setText(company.companyName());
        this.brandCount.setText(Integer.toString(company.brandCount()));
        this.productCount.setText(Integer.toString(company.productCount()));
    }

    @FXML
    protected void getMoreInformation() throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/basdat/basdatchemicals/popups/company-popup.fxml"));
        AnchorPane popupWidget = loader.load();
        CompanyPopup controller = loader.getController();
        controller.setDbConnection(dbConnection);
        controller.insertData(company);

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
