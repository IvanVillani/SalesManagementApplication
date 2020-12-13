package com.ivan.salesapp.services;

import com.ivan.salesapp.constants.ExceptionMessageConstants;
import com.ivan.salesapp.domain.entities.Offer;
import com.ivan.salesapp.domain.entities.OrderProduct;
import com.ivan.salesapp.domain.entities.Record;
import com.ivan.salesapp.domain.models.service.OfferServiceModel;
import com.ivan.salesapp.domain.models.service.RecordServiceModel;
import com.ivan.salesapp.domain.models.view.RecordViewModel;
import com.ivan.salesapp.domain.models.view.Sale;
import com.ivan.salesapp.exceptions.RecordNotFoundException;
import com.ivan.salesapp.repository.OfferRepository;
import com.ivan.salesapp.repository.OrderProductRepository;
import com.ivan.salesapp.repository.RecordRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class RecordService implements IRecordService, ExceptionMessageConstants {
    private final RecordRepository recordRepository;
    private final IUserService iUserService;
    private final OfferRepository offerRepository;
    private final OrderProductRepository orderProductRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public RecordService(RecordRepository recordRepository, IUserService iUserService, OfferRepository offerRepository, OrderProductRepository orderProductRepository, ModelMapper modelMapper) {
        this.recordRepository = recordRepository;
        this.iUserService = iUserService;
        this.offerRepository = offerRepository;
        this.orderProductRepository = orderProductRepository;
        this.modelMapper = modelMapper;
    }

    @Async
    @Override
    public void createRecord(RecordServiceModel recordServiceModel) {
        Record newRecord = this.modelMapper.map(recordServiceModel, Record.class);

        newRecord.setProduct(getOrderProduct(recordServiceModel));

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
    public List<RecordViewModel> retrieveAllRecords() {
        return this.recordRepository.findAll()
                .stream()
                .map(r -> this.modelMapper.map(r, RecordViewModel.class))
                .collect(toList());
    }

    @Override
    public List<Sale> retrieveSalesByUserUsername(String username) throws RecordNotFoundException {
        List<RecordViewModel> records = new ArrayList<>();

        for (Record record : this.recordRepository.findAll()) {
            for (Offer offer : record.getProduct().getOffers()) {
                if (username.equals(offer.getCreator())) {
                    records.add(this.modelMapper.map(record, RecordViewModel.class));
                }
            }
        }

        return extractSalesFromRecords(records, username);
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

        OfferServiceModel offer = findOfferForMatchingCreator(record, username);

        sale.setProduct(record.getProduct().getName());
        sale.setDiscount(offer.getDiscountPrice());
        sale.setQuantity(offer.getQuantity());

        BigDecimal price = offer.getDiscountPrice()
                .multiply(BigDecimal.valueOf(offer.getQuantity().longValue()));

        sale.setPrice(price);
        sale.setRegisterDate(record.getOrder().getRegisterDate());
        sale.setCustomer(record.getOrder().getCustomer());
        return sale;
    }

    private OfferServiceModel findOfferForMatchingCreator(RecordViewModel record, String username) throws RecordNotFoundException {

        return record.getProduct().getOffers()
                .stream()
                .filter(o -> username.equals(o.getCreator()))
                .findFirst().orElseThrow(() ->
                new RecordNotFoundException(String.format(OFFER_BY_USERNAME_NOT_FOUND, username)));

    }

    private OrderProduct getOrderProduct(RecordServiceModel recordServiceModel){
        OrderProduct orderProduct = this.modelMapper.map(recordServiceModel.getProduct(), OrderProduct.class);
        orderProduct.setOffers(getOffers(recordServiceModel));

        return this.orderProductRepository.saveAndFlush(orderProduct);
    }

    private List<Offer> getOffers(RecordServiceModel recordServiceModel){
        List<Offer> offers = new ArrayList<>();

        for (OfferServiceModel offer : recordServiceModel.getProduct().getOffers()) {
            offers.add(this.offerRepository.saveAndFlush(this.modelMapper.map(offer, Offer.class)));
        }

        return offers;
    }
}
