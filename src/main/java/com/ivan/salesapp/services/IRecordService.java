package com.ivan.salesapp.services;

import com.ivan.salesapp.domain.models.service.RecordServiceModel;
import com.ivan.salesapp.domain.models.view.RecordViewModel;
import com.ivan.salesapp.domain.models.view.Sale;
import com.ivan.salesapp.exceptions.RecordNotFoundException;

import java.util.List;

public interface IRecordService {
    void createRecord(RecordServiceModel recordServiceModel);

    RecordViewModel retrieveRecordByOrderId(String id);

    List<RecordViewModel> retrieveRecordsByOrderId(String id) throws RecordNotFoundException;

    List<Sale> retrieveSalesByUserUsername(String username) throws RecordNotFoundException;
}
