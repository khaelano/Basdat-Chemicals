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
import org.basdat.basdatchemicals.entitities.Product;
import org.basdat.basdatchemicals.tablerows.ChemicalRow;
import org.basdat.basdatchemicals.tablerows.ProductCSFRow;

import java.sql.SQLException;
import java.util.Date;

public class ProductPopup {
    private BasdatChemicalsDB dbConnection;
    private TableView<ChemicalRow> productTable;
    private TableView<ProductCSFRow> csfTable;

    public Text productId;
    public Text productName;
    public Text brandName;
    public Text companyName;
    public Text reportDate;
    public Text updateDate;
    public Pagination productChemicalTablePagination;
    public Pagination productCSFTablePagination;

    @FXML
    public void initialize() {
        initializeCompositionTable();
        initializeCSFTable();
    }

    public void initializeCompositionTable() {
        productTable = new TableView<>();
        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        TableColumn<ChemicalRow, String> casNumber = new TableColumn<>("CAS Number");
        TableColumn<ChemicalRow, String> chemicalName = new TableColumn<>("Chemical Name");
        TableColumn<ChemicalRow, Date> reportDateColumn = new TableColumn<>("Report Date");
        TableColumn<ChemicalRow, Date> updateDateColumn = new TableColumn<>("Update Date");

        casNumber.setCellValueFactory(new PropertyValueFactory<>("casNumber"));
        casNumber.prefWidthProperty().bind(productTable.widthProperty().multiply(0.2));

        chemicalName.setCellValueFactory(new PropertyValueFactory<>("chemicalName"));
        chemicalName.prefWidthProperty().bind(productTable.widthProperty().multiply(0.4));

        reportDateColumn.setCellValueFactory(new PropertyValueFactory<>("reportDate"));
        reportDateColumn.prefWidthProperty().bind(productTable.widthProperty().multiply(0.2));

        updateDateColumn.setCellValueFactory(new PropertyValueFactory<>("updateDate"));
        updateDateColumn.prefWidthProperty().bind(productTable.widthProperty().multiply(0.2));

        productTable.getColumns().addAll(casNumber, chemicalName, reportDateColumn, updateDateColumn);
    }

    public void initializeCSFTable() {
        csfTable = new TableView<>();
        csfTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        TableColumn<ProductCSFRow, String> csfDescriptionColumn = new TableColumn<>("CSF Description");

        csfDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("csfDescription"));
        csfDescriptionColumn.prefWidthProperty().bind(productTable.widthProperty().multiply(1.0));

        csfTable.getColumns().addAll(csfDescriptionColumn);

    }

    public void insertData(Product product) throws SQLException {
        productName.setText(product.productName());
        brandName.setText(product.brandName());
        companyName.setText(product.companyName());
        reportDate.setText(product.reportedAt().toString());
        updateDate.setText(product.updatedAt().toString());
        PagedListResult<ChemicalRow> compositionQueryResult = dbConnection.fetchProductChemicals(product.cdphId(), 0);

        productChemicalTablePagination.setPageCount(compositionQueryResult.maxPage());
        productChemicalTablePagination.setPageFactory(pageIndex -> {
            try {
                return createCompositionTable(product.cdphId(), pageIndex);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        PagedListResult<ProductCSFRow> csfQueryResult = dbConnection.fetchProductCFS(product.cdphId(), 0);

        productCSFTablePagination.setPageCount(csfQueryResult.maxPage());
        productCSFTablePagination.setPageFactory(pageIdex -> {
            try {
                return createCSFTable(product.cdphId(), pageIdex);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public BorderPane createCompositionTable(int companyId, int page) throws SQLException {
        PagedListResult<ChemicalRow> queryResult = dbConnection.fetchProductChemicals(companyId, page);
        ObservableList<ChemicalRow> tableRows = FXCollections.observableArrayList(queryResult.content());
        productTable.setItems(tableRows);
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(productTable);

        return borderPane;
    }

    public BorderPane createCSFTable(int companyId, int page) throws SQLException {
        PagedListResult<ProductCSFRow> queryResult = dbConnection.fetchProductCFS(companyId, page);
        ObservableList<ProductCSFRow> tableRows = FXCollections.observableArrayList(queryResult.content());
        csfTable.setItems(tableRows);
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(csfTable);

        return borderPane;
    }

    public void setDbConnection(BasdatChemicalsDB connection) {
        this.dbConnection = connection;
    }
}
