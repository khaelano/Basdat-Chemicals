package org.basdat.basdatchemicals.popups;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import org.basdat.basdatchemicals.BasdatChemicalsDB;
import org.basdat.basdatchemicals.PagedListResult;
import org.basdat.basdatchemicals.entitities.Company;
import org.basdat.basdatchemicals.tablerows.CompanyPopupRow;

import java.sql.SQLException;
import java.util.Date;

public class CompanyPopup {
    private BasdatChemicalsDB dbConnection;
    private TableView<CompanyPopupRow> productTable;

    public Text compName;
    public Text brandCount;
    public Text prodCount;
    public Pagination compProductTablePagination;

    @FXML
    public void initialize() {
        productTable = new TableView<>();
        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        TableColumn<CompanyPopupRow, String> productNameColumn = new TableColumn<>("Product Name");
        TableColumn<CompanyPopupRow, String> brandNameColumn = new TableColumn<>("Brand Name");
        TableColumn<CompanyPopupRow, Date> reportDateColumn = new TableColumn<>("Report Date");
        TableColumn<CompanyPopupRow, Date> updateDateColumn = new TableColumn<>("Update Date");

        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productNameColumn.prefWidthProperty().bind(productTable.widthProperty().multiply(0.4));

        brandNameColumn.setCellValueFactory(new PropertyValueFactory<>("brandName"));
        brandNameColumn.prefWidthProperty().bind(productTable.widthProperty().multiply(0.2));

        reportDateColumn.setCellValueFactory(new PropertyValueFactory<>("reportDate"));
        reportDateColumn.prefWidthProperty().bind(productTable.widthProperty().multiply(0.2));

        updateDateColumn.setCellValueFactory(new PropertyValueFactory<>("updateDate"));
        updateDateColumn.prefWidthProperty().bind(productTable.widthProperty().multiply(0.2));

        productTable.getColumns().addAll(productNameColumn, brandNameColumn, reportDateColumn, updateDateColumn);
    }

    public void insertData(Company company) throws SQLException {
        compName.setText(company.companyName());
        brandCount.setText(Integer.toString(company.brandCount()));
        prodCount.setText(Integer.toString(company.productCount()));
        PagedListResult<CompanyPopupRow> queryResult = dbConnection.fetchCompanyProducts(company.id(), 0);

        compProductTablePagination.setPageCount(queryResult.maxPage());
        compProductTablePagination.setPageFactory(pageIndex -> {
            try {
                return createTable(company.id(), pageIndex);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public BorderPane createTable(int companyId, int page) throws SQLException {
        PagedListResult<CompanyPopupRow> queryResult = dbConnection.fetchCompanyProducts(companyId, page);
        ObservableList<CompanyPopupRow> tableRows = FXCollections.observableArrayList(queryResult.content());
        productTable.setItems(tableRows);
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(productTable);

        return borderPane;
    }

    public void setDbConnection(BasdatChemicalsDB connection) {
        this.dbConnection = connection;
    }
}
