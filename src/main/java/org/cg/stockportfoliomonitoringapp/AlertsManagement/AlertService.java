package org.cg.stockportfoliomonitoringapp.AlertsManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class AlertService {

    @Autowired
    private AlertRepository alertRepo;

    @Autowired
    private StockService stockService;

    @Scheduled(cron = "*/59 * * * * *")
    public void checkAndTriggerAlerts() {
        List<Alert> alerts = alertRepo.findAll();

        Map<String, StocksDTO> stocks = stockService.getAllStocks();

        for (Alert alert : alerts) {
            if (stocks.containsKey(alert.getSymbol())) {
                StocksDTO stock = stocks.get(alert.getSymbol());

                double currentPrice = stock.getCurrentPrice();
                double targetPrice = Double.parseDouble(alert.getTargetPrice());
                double changePercent = ((currentPrice - targetPrice) / targetPrice) * 100;

                String gainLossMessage = changePercent >= 0
                        ? "Gain of " + String.format("%.2f", changePercent) + "%"
                        : "Loss of " + String.format("%.2f", -changePercent) + "%";

                alert.setGainOrLoss(gainLossMessage);
                alertRepo.save(alert);

                System.out.println("Checked alert for " + alert.getSymbol() + ": " + gainLossMessage);
            } else {
                System.out.println("Symbol not found: " + alert.getSymbol());
            }
        }
    }

    public AlertDTO addAlert(Alert alert) {
        double targetPrice = Double.parseDouble(alert.getTargetPrice());
        Map<String, StocksDTO> stocks = stockService.getAllStocks();
        if (stocks.containsKey(alert.getSymbol())) {
            StocksDTO stock = stocks.get(alert.getSymbol());
            double currentPrice = stock.getCurrentPrice();
            double changePercent = ((currentPrice - targetPrice) / targetPrice) * 100;
            String gainLossMessage = changePercent >= 0
                    ? "Gain of " + String.format("%.2f", changePercent) + "%"
                    : "Loss of " + String.format("%.2f", -changePercent) + "%";

            alert.setGainOrLoss(gainLossMessage);
            alertRepo.save(alert);

            AlertDTO dto = new AlertDTO();
            if (currentPrice > targetPrice) {
                dto.setMessage("Alert Triggered -> " + gainLossMessage);
            } else {
                dto.setMessage("Alert Saved (Condition Not Met) -> " + gainLossMessage);
            }
            dto.setMessage("Alert Triggered -> " + gainLossMessage);
            dto.setLocalDateTime(LocalDateTime.now());
            dto.setHttpStatus(HttpStatus.OK);
            dto.setStatus(HttpStatus.OK.value());
            return dto;
        } else {
            throw new RuntimeException("Stock symbol not found in the available stock data.");
        }
    }
}
