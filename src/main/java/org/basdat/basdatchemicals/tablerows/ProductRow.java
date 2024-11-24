package org.basdat.basdatchemicals.tablerows;

import java.util.Date;

public class CompanyProduct {
    String productName;
    String brandName;
    Date reportDate;
    Date updateDate;

    public CompanyProduct(String productName, String brandName, Date reportDate, Date updateDate) {
        this.productName = productName;
        this.brandName = brandName;
        this.reportDate = reportDate;
        this.updateDate = updateDate;
    }

    public String getProductName() {
        return productName;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public String getBrandName() {
        return brandName;
    }
}
