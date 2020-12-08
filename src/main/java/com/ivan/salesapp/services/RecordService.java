package com.ivan.salesapp.services;

import com.ivan.salesapp.constants.ExceptionMessageConstants;
import com.ivan.salesapp.domain.entities.Offer;
import com.ivan.salesapp.domain.entities.Record;
import com.ivan.salesapp.domain.entities.User;
import com.ivan.salesapp.domain.models.service.RecordServiceModel;
import com.ivan.salesapp.domain.models.service.UserServiceModel;
import com.ivan.salesapp.domain.models.view.OfferViewModel;
import com.ivan.salesapp.domain.models.view.RecordViewModel;
import com.ivan.salesapp.domain.models.view.Sale;
import com.ivan.salesapp.exceptions.RecordNotFoundException;
import com.ivan.salesapp.repository.RecordRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class RecordService implements IRecordService, ExceptionMessageConstants {
    private final RecordRepository recordRepository;
    private final IUserService iUserService;
    private final ModelMapper modelMapper;

    @Autowired
    public RecordService(RecordRepository recordRepository, IUserService iUserService, ModelMapper modelMapper) {
        this.recordRepository = recordRepository;
        this.iUserService = iUserService;
        this.modelMapper = modelMapper;
    }

    @Async
    @Override
    public void createRecord(RecordServiceModel recordServiceModel) {
        Record newRecord = this.modelMapper.map(recordServiceModel, Record.class);

        this.recordRepository.saveAndFlush(newRecord);

    }

    @Override
    public List<RecordViewModel> retrieveRecordsByOrderId(String id) throws RecordNotFoundException {
        List<RecordViewModel> records = this.recordRepository.findAll()
                .stream()
                .filter(r -> id.equals(r.getOrder().getId()))
                .map(r -> this.modelMapper.map(r, RecordViewModel.class))
                .collect(toList());

        if (records.isEmpty()) {
            throw new RecordNotFoundException(RECORD_BY_ORDER_ID_NOT_FOUND);
        } else {
            return records;
        }
    }

    @Override
    public RecordViewModel retrieveRecordByOrderId(String id) {
        return null;
    }

    @Override
    public List<Sale> retrieveSalesByUserUsername(String username) throws RecordNotFoundException {
        UserServiceModel user = this.iUserService.findUserByUsername(username);

        List<RecordViewModel> records = new ArrayList<>();

        for (Record record : this.recordRepository.findAll()) {
            for (Offer offer : record.getOffers()) {
                if (user.getId().equals(offer.getDiscount().getCreator().getId())) {
                    records.add(this.modelMapper.map(record, RecordViewModel.class));
                }
            }
        }

        return extractSalesFromRecords(records, user.getUsername());
    }

    private List<Sale> extractSalesFromRecords(List<RecordViewModel> records, String username) throws RecordNotFoundException {
        List<Sale> sales = new ArrayList<>();

        for (RecordViewModel record : records) {
            Sale sale = createSaleFromRecord(record, username);

            sales.add(sale);
        }

        return sales;
    }

    private Sale createSaleFromRecord(RecordViewModel record, String username) throws RecordNotFoundException {
        Sale sale = new Sale();

        OfferViewModel offer = findOfferForMatchingCreator(record, username);

        sale.setProduct(record.getProduct().getName());
        sale.setDiscount(offer.getDiscount().getPrice());
        sale.setQuantity(offer.getQuantity());

        BigDecimal price = offer.getDiscount().getPrice()
                .multiply(BigDecimal.valueOf(offer.getQuantity().longValue()));

        sale.setPrice(price);
        sale.setRegisterDate(record.getOrder().getRegisterDate());
        sale.setCustomer(record.getOrder().getCustomer().getUsername());
        return sale;
    }

    private OfferViewModel findOfferForMatchingCreator(RecordViewModel record, String username) throws RecordNotFoundException {

        return record.getOffers()
                .stream()
                .filter(o -> username.equals(o.getDiscount().getCreator()))
                .findFirst().orElseThrow(() ->
                new RecordNotFoundException(String.format(OFFER_BY_USERNAME_NOT_FOUND, username)));

    }
}
