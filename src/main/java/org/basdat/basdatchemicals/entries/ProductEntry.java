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
import org.basdat.basdatchemicals.entitities.Product;
import org.basdat.basdatchemicals.popups.ProductPopup;

import java.io.IOException;
import java.sql.SQLException;

public class ProductEntry {
    private BasdatChemicalsDB dbConnection;
    private Product product;

    public Text cdphId;
    public Text productName;
    public Text brandName;
    public Text companyName;
    public TitledPane prodEntry;
    public Button prodCompositionButton;

    public void insertData(Product product) {
        this.product = product;
        this.cdphId.setText(Integer.toString(product.cdphId()));
        this.prodEntry.setText(product.productName() + " (Product)");
        this.productName.setText(product.productName());
        this.brandName.setText(product.brandName());
        this.companyName.setText(product.companyName());
    }

    @FXML
    protected void getMoreInformation() throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/basdat/basdatchemicals/popups/product-popup.fxml"));
        AnchorPane popupWidget = loader.load();
        ProductPopup controller = loader.getController();
        controller.setDbConnection(dbConnection);
        controller.insertData(product);

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
