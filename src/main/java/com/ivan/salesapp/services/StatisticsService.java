package com.ivan.salesapp.services;

import com.ivan.salesapp.domain.models.service.RecordServiceModel;
import com.ivan.salesapp.domain.models.view.OfferViewModel;
import com.ivan.salesapp.domain.models.view.RecordViewModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsService implements IStatisticsService{
    private final IRecordService iRecordService;
    private final ModelMapper modelMapper;

    @Autowired
    public StatisticsService(IRecordService iRecordService, ModelMapper modelMapper) {
        this.iRecordService = iRecordService;
        this.modelMapper = modelMapper;
    }


    @Override
    public Map<String, Double> getStatisticsGraphDataByProductsSold() {
        Map<String, Double> data = new LinkedHashMap<>();

        List<OfferViewModel> offers =  this.iRecordService.retrieveAllRecords()
                .stream()
                .map(RecordViewModel::getOffers)
                .flatMap(List::stream)
                .collect(Collectors.toList());

         int soldQuantity = 0;

        for (OfferViewModel offer : offers) {
            soldQuantity += offer.getQuantity();
        }

        for (OfferViewModel offer : offers) {
            String creator = offer.getDiscount().getCreator();
            if(data.containsKey(creator)){
                Double percent = data.get(creator) + (offer.getQuantity() * 100 / soldQuantity);
                data.put(creator, percent);
            }else{
                Double percent = Double.valueOf(offer.getQuantity()) * 100 / soldQuantity;
                data.put(creator, percent);
            }
        }

        return data;
    }

    @Override
    public Map<String, Double> getStatisticsGraphDataByIncome() {
        Map<String, Double> data = new LinkedHashMap<>();

        List<OfferViewModel> offers =  this.iRecordService.retrieveAllRecords()
                .stream()
                .map(RecordViewModel::getOffers)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        BigDecimal fullIncome = new BigDecimal(0);

        for (OfferViewModel offer : offers) {
            fullIncome = fullIncome.add(offer.getDiscount().getPrice().multiply(BigDecimal.valueOf(offer.getQuantity())));
        }

        for (OfferViewModel offer : offers) {
            String creator = offer.getDiscount().getCreator();
            if(data.containsKey(creator)){
                Double percent = data.get(creator) + Double.parseDouble(String.valueOf((offer.getDiscount().getPrice()
                        .multiply(BigDecimal.valueOf(offer.getQuantity()))
                        .multiply(new BigDecimal(100)).divide(fullIncome, 2, RoundingMode.CEILING))));
                data.put(creator, percent);
            }else{
                Double percent = Double.parseDouble(String.valueOf((offer.getDiscount().getPrice()
                        .multiply(BigDecimal.valueOf(offer.getQuantity()))
                        .multiply(new BigDecimal(100)).divide(fullIncome, 2, RoundingMode.CEILING))));
                data.put(creator, percent);
            }
        }

        return data;
    }

    @Override
    public Map<String, Integer> getStatisticsGraphDataByTime() {
        Map<String, Integer> data = new LinkedHashMap<>();

        List<RecordViewModel> records =  this.iRecordService.retrieveAllRecords();

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime sevenDaysBefore = LocalDateTime.now().minusDays(7);

        for (RecordViewModel record : records) {
            LocalDateTime date = record.getOrder().getRegisterDate();

            if((date.isAfter(sevenDaysBefore) || date.isEqual(sevenDaysBefore)) &&
                    (date.isBefore(now) || date.isEqual(now))){
                String day = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault());

                if (data.containsKey(day)){
                    data.put(day, data.get(day) + record.getFullQuantity());
                }else {
                    data.put(day, record.getFullQuantity());
                }
            }
        }
        return data;
    }
}
