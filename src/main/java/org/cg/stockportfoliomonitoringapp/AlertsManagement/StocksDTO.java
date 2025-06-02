package org.cg.stockportfoliomonitoringapp.AlertsManagement;

import lombok.Data;

@Data
public class StocksDTO {
    private Long stockId;
    private String companyName;
    private String symbol;
    private String sector;
    private double CurrentPrice;
}
