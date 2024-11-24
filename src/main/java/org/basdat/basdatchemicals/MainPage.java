package org.basdat.basdatchemicals;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.basdat.basdatchemicals.entries.ChemicalEntry;
import org.basdat.basdatchemicals.entries.CompanyEntry;
import org.basdat.basdatchemicals.entries.ProductEntry;
import org.basdat.basdatchemicals.entitities.Chemical;
import org.basdat.basdatchemicals.entitities.Company;
import org.basdat.basdatchemicals.entitities.Product;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;

public class MainPage {
    public CheckBox chemicalSearchToggle;
    public CheckBox companySearchToggle;
    public CheckBox productSearchToggle;
    public Pagination searchResultPagination;
    private BasdatChemicalsDB dbConnection;

    public TextField searchBar;
    public Button searchButton;

    @FXML
    protected void search() throws SQLException {
        String keyword = searchBar.getText();

        if(keyword.isBlank()) {
            return;
        }

        int maxPage = -1;
        if(chemicalSearchToggle.isSelected()) {
            PagedListResult<Chemical> result =  dbConnection.searchChemical(keyword, 0);
            maxPage = Math.max(result.maxPage(), maxPage);
        }

        if(productSearchToggle.isSelected()) {
            PagedListResult<Product> result = dbConnection.searchProduct(keyword, 0);
            maxPage = Math.max(result.maxPage(), maxPage);
        }

        if(companySearchToggle.isSelected()) {
            PagedListResult<Company> result = dbConnection.searchCompany(keyword, 0);
            maxPage = Math.max(result.maxPage(), maxPage);
        }

        if (maxPage < 0) return;
        searchResultPagination.setPageCount(maxPage);
        searchResultPagination.setPageFactory(index -> {
            try {
                return generateList(keyword, index);
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private HBox generateList(String keyword, int index) throws SQLException, IOException {
        HBox hbox = new HBox();
//        hbox.setStyle("-fx-border-color: black; -fx-border-width: 1;");
        VBox searchList = new VBox();

        if(chemicalSearchToggle.isSelected()) searchList.getChildren().addAll(searchChemicals(keyword, index));
        if(productSearchToggle.isSelected()) searchList.getChildren().addAll(searchProducts(keyword, index));
        if(companySearchToggle.isSelected()) searchList.getChildren().addAll(searchCompanies(keyword, index));

        HBox.setHgrow(searchList, Priority.ALWAYS);
        hbox.getChildren().add(searchList);
        return hbox;
    }

    protected LinkedList<HBox> searchChemicals(String keyword, int page) throws SQLException, IOException {
        PagedListResult<Chemical> chemList= dbConnection.searchChemical(keyword, page);
        LinkedList<HBox> widgetList = new LinkedList<>();

        for (Chemical chemical : chemList.content()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("entries/chemical-entry.fxml"));
            HBox chemEntry = loader.load();
            ChemicalEntry controller = loader.getController();
            controller.setDbConnection(dbConnection);
            controller.insertData(chemical);

            widgetList.add(chemEntry);
        }

        return widgetList;
    }

    protected LinkedList<HBox> searchProducts(String keyword, int page) throws SQLException, IOException {
        PagedListResult<Product> chemList= dbConnection.searchProduct(keyword, page);
        LinkedList<HBox> widgetList = new LinkedList<>();

        for (Product product : chemList.content()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("entries/product-entry.fxml"));
            HBox prodEntry = loader.load();
            ProductEntry controller = loader.getController();
            controller.setDbConnection(dbConnection);
            controller.insertData(product);

            widgetList.add(prodEntry);
        }

        return widgetList;
    }

    protected LinkedList<HBox> searchCompanies(String keyword, int page) throws SQLException, IOException {
        PagedListResult<Company> chemList= dbConnection.searchCompany(keyword, page);
        LinkedList<HBox> widgetList = new LinkedList<>();

        for (Company company : chemList.content()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("entries/company-entry.fxml"));
            HBox prodEntry = loader.load();
            CompanyEntry controller = loader.getController();
            controller.setDbConnection(dbConnection);
            controller.insertData(company);

            widgetList.add(prodEntry);
        }

        return widgetList;
    }

    public void setDbConnection(BasdatChemicalsDB connection) {
        this.dbConnection = connection;
    }
}