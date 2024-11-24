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
import org.basdat.basdatchemicals.tablerows.ChemicalPopupRow;

import javax.lang.model.AnnotatedConstruct;
import java.sql.SQLException;
import java.util.Date;

public class ChemicalPopupController {
    private BasdatChemicalsDB dbConnection;
    private String casNumber;
    private int currPage = 0;
    private int maxPage = Integer.MAX_VALUE;

    public Text chemName;
    public Text chemCasNum;
    public Pagination tablePagination;
    public TableView<ChemicalPopupRow> productsTable;
    public TableColumn<ChemicalPopupRow, String> prodNameColumn;
    public TableColumn<ChemicalPopupRow, String> brandNameColumn;
    public TableColumn<ChemicalPopupRow, Date> reportDateColumn;
    public TableColumn<ChemicalPopupRow, Date> updateDateColumn;

    @FXML
    public void initialize() {
        prodNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        brandNameColumn.setCellValueFactory(new PropertyValueFactory<>("brandName"));
        reportDateColumn.setCellValueFactory(new PropertyValueFactory<>("reportDate"));
        updateDateColumn.setCellValueFactory(new PropertyValueFactory<>("updateDate"));
    }

    public void insertData(Chemical chemical) throws SQLException {
        this.chemName.setText(chemical.name());
        this.chemCasNum.setText(chemical.casNumber());
        this.casNumber = chemical.casNumber();

        PagedListResult<ChemicalPopupRow> queryResult = dbConnection.fetchProducts(casNumber, 0);
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
        PagedListResult<ChemicalPopupRow> queryResult = dbConnection.fetchProducts(casNumber, page);
        ObservableList<ChemicalPopupRow> list = FXCollections.observableArrayList(queryResult.content());
        productsTable.setItems(list);
//        productsTable.refresh();
        return new AnchorPane(productsTable);
    }

    public void setDbConnection(BasdatChemicalsDB connection) {
        this.dbConnection = connection;
    }
}
