package org.basdat.basdatchemicals.popups;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.basdat.basdatchemicals.BasdatChemicalsDB;
import org.basdat.basdatchemicals.PagedListResult;
import org.basdat.basdatchemicals.entitities.Chemical;
import org.basdat.basdatchemicals.tablerows.ProductRow;

import java.sql.SQLException;
import java.util.Date;

public class ChemicalPopup {
    private BasdatChemicalsDB dbConnection;
    private String casNumber;
    private int currPage = 0;
    private int maxPage = Integer.MAX_VALUE;

    public Text chemName;
    public Text chemCasNum;
    public Pagination tablePagination;
    public TableView<ProductRow> productsTable;
    public TableColumn<ProductRow, String> prodNameColumn;
    public TableColumn<ProductRow, String> brandNameColumn;
    public TableColumn<ProductRow, Date> reportDateColumn;
    public TableColumn<ProductRow, Date> updateDateColumn;

    @FXML
    public void initialize() {
        prodNameColumn.setCellValueFactory(new PropertyValueFactory<ProductRow, String>("productName"));
        brandNameColumn.setCellValueFactory(new PropertyValueFactory<ProductRow, String>("brandName"));
        reportDateColumn.setCellValueFactory(new PropertyValueFactory<ProductRow, Date>("reportDate"));
        updateDateColumn.setCellValueFactory(new PropertyValueFactory<ProductRow, Date>("updateDate"));
    }

    public void insertData(Chemical chemical) throws SQLException {
        this.chemName.setText(chemical.chemicalName());
        this.chemCasNum.setText(chemical.casNumber());
        this.casNumber = chemical.casNumber();

        PagedListResult<ProductRow> queryResult = dbConnection.fetchChemicalProducts(casNumber, 0);
        this.tablePagination.setPageCount(queryResult.maxPage());
        this.tablePagination.setPageFactory(pageIndex -> {
            try {
                return setTable(casNumber, pageIndex);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public AnchorPane setTable(String casNumber, int page) throws SQLException {
        PagedListResult<ProductRow> queryResult = dbConnection.fetchChemicalProducts(casNumber, page);
        ObservableList<ProductRow> list = FXCollections.observableArrayList(queryResult.content());
        productsTable.setItems(list);
//        productsTable.refresh();
        return new AnchorPane(productsTable);
    }

    public void setDbConnection(BasdatChemicalsDB connection) {
        this.dbConnection = connection;
    }
}
