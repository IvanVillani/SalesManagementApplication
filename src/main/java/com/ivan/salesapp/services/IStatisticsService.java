package com.ivan.salesapp.services;

import java.util.Map;

public interface IStatisticsService {

    Map<String, Double> getStatisticsGraphDataByProductsSold();

    Map<String, Double> getStatisticsGraphDataByIncome();

    Map<String, Integer> getStatisticsGraphDataByTime();
}
