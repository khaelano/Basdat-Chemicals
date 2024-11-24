package org.basdat.basdatchemicals.tablerows;

import java.util.Date;

public class ChemicalRow {
    String casNumber;
    String chemicalName;
    Date reportDate;

    public ChemicalRow(String casNumber, String chemicalName, Date reportDate, Date updateDate) {
        this.casNumber = casNumber;
        this.chemicalName = chemicalName;
        this.reportDate = reportDate;
        this.updateDate = updateDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public String getChemicalName() {
        return chemicalName;
    }

    public String getCasNumber() {
        return casNumber;
    }

    Date updateDate;
}
