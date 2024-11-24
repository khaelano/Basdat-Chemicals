package org.basdat.basdatchemicals.entitities;

import java.util.Date;

public record Product(int cdphId, String productName, String brandName, String companyName, Date reportedAt,
                      Date updatedAt,
                      int ChemicalCount) {
}
