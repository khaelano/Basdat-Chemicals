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
import org.basdat.basdatchemicals.tablerows.ProductRow;

import java.sql.SQLException;
import java.util.Date;

public class CompanyPopup {
    private BasdatChemicalsDB dbConnection;
    private TableView<ProductRow> productTable;

    public Text companyId;
    public Text companyName;
    public Text brandCount;
    public Text productCount;
    public Pagination productChemicalsTablePagination;

    @FXML
    public void initialize() {
        productTable = new TableView<>();
        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        TableColumn<ProductRow, String> productNameColumn = new TableColumn<>("Product Name");
        TableColumn<ProductRow, String> brandNameColumn = new TableColumn<>("Brand Name");
        TableColumn<ProductRow, Date> reportDateColumn = new TableColumn<>("Report Date");
        TableColumn<ProductRow, Date> updateDateColumn = new TableColumn<>("Update Date");

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
        companyName.setText(company.companyName());
        brandCount.setText(Integer.toString(company.brandCount()));
        productCount.setText(Integer.toString(company.productCount()));
        PagedListResult<ProductRow> queryResult = dbConnection.fetchCompanyProducts(company.id(), 0);

        productChemicalsTablePagination.setPageCount(queryResult.maxPage());
        productChemicalsTablePagination.setPageFactory(pageIndex -> {
            try {
                return createTable(company.id(), pageIndex);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public BorderPane createTable(int companyId, int page) throws SQLException {
        PagedListResult<ProductRow> queryResult = dbConnection.fetchCompanyProducts(companyId, page);
        ObservableList<ProductRow> tableRows = FXCollections.observableArrayList(queryResult.content());
        productTable.setItems(tableRows);
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(productTable);

        return borderPane;
    }

    public void setDbConnection(BasdatChemicalsDB connection) {
        this.dbConnection = connection;
    }
}
