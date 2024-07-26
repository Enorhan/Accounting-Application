package com.cydeo.service;

import java.math.BigDecimal;
import java.util.Map;

public interface DashboardService {
    Map<String,BigDecimal> getTotalCostAndSalesAndProfit_loss();

}
