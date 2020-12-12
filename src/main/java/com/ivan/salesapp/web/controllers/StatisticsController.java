package com.ivan.salesapp.web.controllers;

import com.ivan.salesapp.services.IStatisticsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/statistics")
public class StatisticsController {
    private final IStatisticsService iStatisticsService;
    private final ModelMapper modelMapper;

    @Autowired
    public StatisticsController(IStatisticsService iStatisticsService, ModelMapper modelMapper) {
        this.iStatisticsService = iStatisticsService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/get-statistics")
    public Model barGraph(Model model) {
        Map<String, Double> surveyMapByProductsSold = this.iStatisticsService.getStatisticsGraphDataByProductsSold();
        Map<String, Double> surveyMapByIncome = this.iStatisticsService.getStatisticsGraphDataByIncome();
        Map<String, Integer> surveyMapByTime = this.iStatisticsService.getStatisticsGraphDataByTime();

        int max = 0;

        for (Integer value : surveyMapByTime.values()) {
            if (value > max){
                max = value;
            }
        }

        model.addAttribute("surveyMapByProductsSold", surveyMapByProductsSold);
        model.addAttribute("surveyMapByIncome", surveyMapByIncome);
        model.addAttribute("surveyMapByTime", surveyMapByTime);
        model.addAttribute("max", max + 2);

        return model;
    }

    @GetMapping("/pie-chart")
    public Model pieChart(Model model) {
        model.addAttribute("pass", 50);
        model.addAttribute("fail", 50);
        return model;
    }

}