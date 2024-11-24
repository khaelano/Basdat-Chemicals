package org.basdat.basdatchemicals;

import org.basdat.basdatchemicals.entitities.Chemical;
import org.basdat.basdatchemicals.entitities.Company;
import org.basdat.basdatchemicals.entitities.Product;
import org.basdat.basdatchemicals.tablerows.ProductCSFRow;
import org.basdat.basdatchemicals.tablerows.ProductRow;
import org.basdat.basdatchemicals.tablerows.ChemicalRow;

import java.sql.*;
import java.util.LinkedList;

public class BasdatChemicalsDB {
    Connection connection;
    int pageLength = 30;

    public BasdatChemicalsDB(
            String hostname,
            int port_number,
            String database_name,
            String username,
            String password
    ) throws SQLException {
        connection = DriverManager.getConnection(
                String.format("jdbc:sqlserver://%s:%d;databaseName=%s;user=%s;password=%s;encrypt=false;trust_server_certificate=true;",
                        hostname,
                        port_number,
                        database_name,
                        username,
                        password
                ));
    }

    PagedListResult<Chemical> searchChemical(String keyword, int page) throws SQLException {
        // query untuk mengambil jumlah baris query agar dapat digunakan untuk menentukan batas maksimal halaman pencarian
        PreparedStatement countStatemtnt = connection.prepareStatement(
                "SELECT COUNT(DISTINCT CAS.CasNumber) FROM CAS JOIN Chemical C ON CAS.CasId = C.CasId WHERE C.ChemicalName = ? OR CAS.CasNumber = ?;"
        );
        countStatemtnt.setString(1, keyword);
        countStatemtnt.setString(2, keyword);
        ResultSet countResult = countStatemtnt.executeQuery();
        countResult.next();
        int totalRowCount = countResult.getInt(1);
        int totalPage = (int) Math.ceil((double) totalRowCount / (double) pageLength);
        int offset = page * pageLength;

        // query untuk mengambil data sebenarnya
        PreparedStatement statement = connection.prepareStatement(
                "SELECT DISTINCT C.ChemicalName chemicalName, CAS.CasNumber casNumber FROM CAS JOIN Chemical C ON CAS.CasId = C.CasId WHERE C.ChemicalName = ? OR CAS.CasNumber = ? ORDER BY c.ChemicalName OFFSET ? ROWS FETCH NEXT ? ROWS ONLY;"
        );
        statement.setString(1, keyword);
        statement.setString(2, keyword);
        statement.setInt(3, offset);
        statement.setInt(4, pageLength);
        ResultSet result = statement.executeQuery();

        LinkedList<Chemical> chemicals = new LinkedList<>();
        while (result.next()) {
            String chemicalName = result.getString("chemicalName");
            String casNumber = result.getString("casNumber");

            chemicals.add(new Chemical(chemicalName, casNumber));
        }

        return new PagedListResult<>(page, totalPage, chemicals);
    }

    PagedListResult<Product> searchProduct(String keyword, int page) throws SQLException {
        // query untuk mengambil jumlah baris query agar dapat digunakan untuk menentukan batas maksimal halaman pencarian
        PreparedStatement countStatemtnt = connection.prepareStatement(
                "SELECT COUNT(P.CDPHId) FROM Product P JOIN BrandName BN ON P.brandname = BN.brandname JOIN Company C ON BN.CompanyId = C.CompanyId WHERE P.ProductName = ? OR P.CDPHId = ?;"
        );
        countStatemtnt.setString(1, keyword);
        countStatemtnt.setString(2, keyword);
        ResultSet countResult = countStatemtnt.executeQuery();
        countResult.next();
        int totalRowCount = countResult.getInt(1);
        int totalPage = (int) Math.ceil((double) totalRowCount / (double) pageLength);
        int offset = page * pageLength;

        // query untuk mengambil data sebenarnya
        PreparedStatement statement = connection.prepareStatement(
                "SELECT P.CDPHId cdphId, P.ProductName productName, P.brandname brandName, P.InitialDateReported reportDate, P.MostRecentDateReported updateDate, P.ChemicalCount chemicalCount, C.CompanyName AS companyName FROM Product P JOIN BrandName BN ON P.brandname = BN.brandname JOIN Company C ON BN.CompanyId = C.CompanyId WHERE P.ProductName = ? OR P.CDPHId = ? ORDER BY P.ProductName OFFSET ? ROWS FETCH NEXT ? ROWS ONLY;"
        );
        statement.setString(1, keyword);
        statement.setString(2, keyword);
        statement.setInt(3, offset);
        statement.setInt(4, pageLength);
        ResultSet result = statement.executeQuery();

        LinkedList<Product> products = new LinkedList<>();
        while (result.next()) {
            int cdphId = result.getInt("cdphId");
            String productName = result.getString("productName");
            String brandName = result.getString("brandName");
            String companyName = result.getString("companyName");
            Date reportDate = result.getDate("reportDate");
            Date updateDate = result.getDate("updateDate");
            int chemicalCount = result.getInt("chemicalCount");

            products.add(new Product(cdphId, productName, brandName, companyName, reportDate, updateDate, chemicalCount));
        }

        return new PagedListResult<>(page, totalPage, products);
    }

    PagedListResult<Company> searchCompany(String keyword, int page) throws SQLException {
        // query untuk mengambil jumlah baris query agar dapat digunakan untuk menentukan batas maksimal halaman pencarian
        PreparedStatement countStatement = connection.prepareStatement(
                "SELECT COUNT(Cn.CompanyId) FROM Company Cn WHERE Cn.CompanyName = ? OR Cn.CompanyId = ?;"
        );
        countStatement.setString(1, keyword);
        countStatement.setString(2, keyword);
        ResultSet countResult = countStatement.executeQuery();
        countResult.next();
        int totalRowCount = countResult.getInt(1);
        int totalPage = (int) Math.ceil((double) totalRowCount / (double) pageLength);
        int offset = page * pageLength;

        // query untuk mengambil data sebenarnya
        PreparedStatement statement = connection.prepareStatement(
                "SELECT Cn.CompanyId companyId, Cn.CompanyName companyName, (SELECT COUNT(BrandName) FROM BrandName Bn WHERE Cn.CompanyId = Bn.CompanyId) brandCount, (SELECT COUNT(P.CDPHId) FROM Product P JOIN BrandName B ON P.brandname = B.brandname JOIN Company C ON B.CompanyId = C.CompanyId WHERE C.CompanyId = Cn.CompanyId) productCount FROM Company Cn WHERE Cn.CompanyName = ? OR Cn.CompanyId = ? ORDER BY Cn.CompanyId OFFSET ? ROWS FETCH NEXT ? ROWS ONLY;"
        );
        statement.setString(1, keyword);
        statement.setString(2, keyword);
        statement.setInt(3, offset);
        statement.setInt(4, pageLength);

        ResultSet result = statement.executeQuery();
        LinkedList<Company> companies = new LinkedList<>();

        while (result.next()) {
            int companyId = result.getInt("companyId");
            String companyName = result.getString("companyName");
            int brandCount = result.getInt("brandCount");
            int productCount = result.getInt("productCount");

            companies.add(new Company(companyId, companyName, brandCount, productCount));
        }

        return new PagedListResult<>(page, totalPage, companies);
    }

    public PagedListResult<ProductRow> fetchChemicalProducts(String casNum, int page) throws SQLException {
        // query untuk mengambil jumlah baris query agar dapat digunakan untuk menentukan batas maksimal halaman pencarian
        PreparedStatement countStatement = connection.prepareStatement(
                "SELECT DISTINCT COUNT(*) FROM Contain Ctn JOIN Product P ON P.CDPHId = Ctn.CDPHId JOIN Chemical Ch ON Ch.ChemicalId = Ctn.ChemicalId JOIN CAS ON CAS.CasId = Ch.CasId WHERE CAS.CasNumber = ?;"
        );
        countStatement.setString(1, casNum);
        ResultSet countResult = countStatement.executeQuery();
        countResult.next();
        int totalRowCount = countResult.getInt(1);
        int totalPage = (int) Math.ceil((double) totalRowCount / (double) pageLength);
        int offset = page * pageLength;

        // query untuk mengambil data sebenarnya
        PreparedStatement statement = connection.prepareStatement(
                "SELECT DISTINCT P.ProductName ProdName, P.brandname BrandName, Ch.ChemicalCreatedAt ReportedAt, Ch.ChemicalUpdatedAt UpdatedAt FROM Contain Ctn JOIN Product P ON P.CDPHId = Ctn.CDPHId JOIN Chemical Ch ON Ch.ChemicalId = Ctn.ChemicalId JOIN CAS ON CAS.CasId = Ch.CasId WHERE CAS.CasNumber = ? ORDER BY ProdName OFFSET ? ROWS FETCH NEXT ? ROWS ONLY;"
        );
        statement.setString(1, casNum);
        statement.setInt(2, offset);
        statement.setInt(3, pageLength);
        ResultSet queryResult = statement.executeQuery();

        LinkedList<ProductRow> rows = new LinkedList<>();
        while (queryResult.next()) {
            String productName = queryResult.getString("ProdName");
            String brandName = queryResult.getString("BrandName");
            Date reportDate = queryResult.getDate("ReportedAt");
            Date updateDate = queryResult.getDate("UpdatedAt");

            rows.add(new ProductRow(productName, brandName, reportDate, updateDate));
        }

        return new PagedListResult<>(page, totalPage, rows);
    }

    public PagedListResult<ProductRow> fetchCompanyProducts(int companyId, int page) throws SQLException {
        // query untuk mengambil jumlah baris query agar dapat digunakan untuk menentukan batas maksimal halaman pencarian
        PreparedStatement countStatement = connection.prepareStatement(
                "SELECT COUNT(*) FROM Product P JOIN BrandName BN ON P.brandname = BN.brandname WHERE BN.CompanyId = ?;"
        );
        countStatement.setInt(1, companyId);
        ResultSet countResult = countStatement.executeQuery();
        countResult.next();
        int totalRowCount = countResult.getInt(1);
        int totalPage = (int) Math.ceil((double) totalRowCount / (double) pageLength);
        int offset = page * pageLength;

        // query untuk mengambil data sebenarnya
        PreparedStatement statement = connection.prepareStatement(
                "SELECT P.ProductName productName, P.brandname brandName, P.InitialDateReported reportDate, P.MostRecentDateReported updateDate FROM Product P JOIN BrandName BN ON P.brandname = BN.brandname WHERE BN.CompanyId = ? ORDER BY P.brandname OFFSET ? ROWS FETCH NEXT ? ROWS ONLY;"
        );
        statement.setInt(1, companyId);
        statement.setInt(2, offset);
        statement.setInt(3, pageLength);
        ResultSet queryResult = statement.executeQuery();

        LinkedList<ProductRow> rows = new LinkedList<>();
        while (queryResult.next()) {
            String productName = queryResult.getString("productName");
            String brandName = queryResult.getString("brandName");
            Date reportDate = queryResult.getDate("reportDate");
            Date updateDate = queryResult.getDate("updateDate");

            rows.add(new ProductRow(productName, brandName, reportDate, updateDate));
        }

        return new PagedListResult<>(page, totalPage, rows);
    }

    public PagedListResult<ChemicalRow> fetchProductChemicals(int cdphId, int page) throws SQLException {
        // query untuk mengambil jumlah baris query agar dapat digunakan untuk menentukan batas maksimal halaman pencarian
        PreparedStatement countStatement = connection.prepareStatement(
                "SELECT COUNT(Cn.ChemicalId) FROM Contain Cn JOIN Chemical C ON Cn.ChemicalId = C.ChemicalId JOIN CAS ON CAS.CasId = C.CasId WHERE Cn.CDPHId = ?;"
        );
        countStatement.setInt(1, cdphId);
        ResultSet countResult = countStatement.executeQuery();
        countResult.next();
        int totalRowCount = countResult.getInt(1);
        int totalPage = (int) Math.ceil((double) totalRowCount / (double) pageLength);
        int offset = page * pageLength;

        // query untuk mengambil data sebenarnya
        PreparedStatement statement = connection.prepareStatement(
                "SELECT C.ChemicalName chemicalName, CAS.CasNumber casNumber, C.ChemicalCreatedAt reportDate, C.ChemicalUpdatedAt updateDate FROM Contain Cn JOIN Chemical C ON Cn.ChemicalId = C.ChemicalId JOIN CAS ON CAS.CasId = C.CasId WHERE Cn.CDPHId = ? ORDER BY C.ChemicalName OFFSET ? ROWS FETCH NEXT ? ROWS ONLY;"
        );
        statement.setInt(1, cdphId);
        statement.setInt(2, offset);
        statement.setInt(3, pageLength);
        ResultSet queryResult = statement.executeQuery();

        LinkedList<ChemicalRow> rows = new LinkedList<>();
        while (queryResult.next()) {
            String casNumber = queryResult.getString("casNumber");
            String chemicalName = queryResult.getString("chemicalName");
            Date reportDate = queryResult.getDate("reportDate");
            Date updateDate = queryResult.getDate("updateDate");

            rows.add(new ChemicalRow(casNumber, chemicalName, reportDate, updateDate));
        }

        return new PagedListResult<>(page, totalPage, rows);
    }

    public PagedListResult<ProductCSFRow> fetchProductCFS(int cdphId, int page) throws SQLException {
        // query untuk mengambil jumlah baris query agar dapat digunakan untuk menentukan batas maksimal halaman pencarian
        PreparedStatement countStatement = connection.prepareStatement(
                "SELECT COUNT(CSFId) FROM CSF WHERE CDPHId = ?;"
        );
        countStatement.setInt(1, cdphId);
        ResultSet countResult = countStatement.executeQuery();
        countResult.next();
        int totalRowCount = countResult.getInt(1);
        int totalPage = (int) Math.ceil((double) totalRowCount / (double) pageLength);
        int offset = page * pageLength;

        // query untuk mengambil data sebenarnya
        PreparedStatement statement = connection.prepareStatement(
                "SELECT CSF csfDescription FROM CSF WHERE CDPHId = ? ORDER BY CSF OFFSET ? ROWS FETCH NEXT ? ROWS ONLY;"
        );
        statement.setInt(1, cdphId);
        statement.setInt(2, offset);
        statement.setInt(3, pageLength);
        ResultSet queryResult = statement.executeQuery();

        LinkedList<ProductCSFRow> rows = new LinkedList<>();
        while (queryResult.next()) {
            String csfDescription = queryResult.getString("csfDescription");

            rows.add(new ProductCSFRow(csfDescription));
        }

        return new PagedListResult<>(page, totalPage, rows);
    }
}
