package org.basdat.basdatchemicals;

import org.basdat.basdatchemicals.entitities.Chemical;
import org.basdat.basdatchemicals.entitities.Company;
import org.basdat.basdatchemicals.entitities.Product;

import java.sql.*;
import java.util.LinkedList;

public class BasdatChemDB {
    Connection connection;

    public BasdatChemDB(
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

    LinkedList<Chemical> searchChemical(String keyword) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT DISTINCT c.ChemicalName name, CAS.CasNumber casNumber FROM Chemical c JOIN CAS ON c.CasId = CAS.CasId WHERE c.ChemicalName = ?");
        statement.setString(1, keyword);
        ResultSet result = statement.executeQuery();

        LinkedList<Chemical> chemicals = new LinkedList<>();

        while (result.next()) {
            String name = result.getString("name");
            String casNumber = result.getString("casNumber");

            chemicals.add(new Chemical(name, casNumber));
        }

        return chemicals;
    }

    LinkedList<Product> searchProduct(String keyword) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT DISTINCT P.CDPHId AS prodCDPHId, P.ProductName AS prodName, P.brandname AS prodBrandName, P.MostRecentDateReported AS prodUpdateDate, P.InitialDateReported AS prodReportDate, P.ChemicalCount AS prodChemCount, C.CompanyName AS prodCompName FROM Product P JOIN BrandName BN ON P.brandname = BN.brandname JOIN Company C ON BN.CompanyId = C.CompanyId WHERE P.ProductName = ?");
        statement.setString(1, keyword);
        ResultSet result = statement.executeQuery();
        LinkedList<Product> products = new LinkedList<>();

        while (result.next()) {
            int id = result.getInt("prodCDPHId");
            String name = result.getString("prodName");
            String brandName = result.getString("prodBrandName");
            String companyName = result.getString("prodCompName");
            Date updatedAt = result.getDate("prodUpdateDate");
            Date reportedAt = result.getDate("prodReportDate");
            int chemicalCount = result.getInt("prodChemCount");

            products.add(new Product(id, name, brandName, companyName, updatedAt, reportedAt, chemicalCount));
        }

        return products;
    }

    LinkedList<Company> searchCompany(String keyword) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT DISTINCT CompanyId, CompanyName FROM Company WHERE CompanyName = ?");
        statement.setString(1, keyword);
        ResultSet result = statement.executeQuery();
        LinkedList<Company> companies = new LinkedList<>();

        while (result.next()) {
            int id = result.getInt("CompanyId");
            String name = result.getString("CompanyName");

            companies.add(new Company(id, name));
        }

        return companies;
    }
}
