package com.ivan.salesapp.integration.services;

import com.ivan.salesapp.domain.entities.Order;
import com.ivan.salesapp.domain.entities.Record;
import com.ivan.salesapp.domain.entities.User;
import com.ivan.salesapp.domain.models.service.RecordServiceModel;
import com.ivan.salesapp.domain.models.service.RoleServiceModel;
import com.ivan.salesapp.domain.models.service.UserServiceModel;
import com.ivan.salesapp.domain.models.view.RecordViewModel;
import com.ivan.salesapp.exceptions.RecordNotFoundException;
import com.ivan.salesapp.repository.RecordRepository;
import com.ivan.salesapp.repository.UserRepository;
import com.ivan.salesapp.services.IDiscountService;
import com.ivan.salesapp.services.IRecordService;
import com.ivan.salesapp.services.IRoleService;
import com.ivan.salesapp.services.IUserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RecordServiceTest {
    @Autowired
    IRecordService iRecordService;

    @MockBean
    private RecordRepository mockRecordRepository;

    @MockBean
    private IUserService mockIUserService;

    private List<Record> records;

    @Before
    public void setupTest() {
        records = new ArrayList<>();
        when(mockRecordRepository.findAll())
                .thenReturn(records);
    }

    @Test
    public void findRecordsByOrderId_whenOneRecordMatches_returnRecord() throws RecordNotFoundException {
        records.add(new Record(){{ setOrder(new Order(){{ setId("test-id"); }}); }});

        List<RecordViewModel> result = iRecordService.retrieveRecordsByOrderId("test-id");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("test-id", result.get(0).getOrder().getId());
    }

    @Test
    public void findAllRecords_whenTwoRecordsPresent_returnTwoRecords() throws RecordNotFoundException {
        records.add(new Record(){{ setOrder(new Order(){{ setId("test-id1"); }}); }});
        records.add(new Record(){{ setOrder(new Order(){{ setId("test-id2"); }}); }});

        List<RecordViewModel> result = iRecordService.retrieveAllRecords();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals("test-id1", result.get(0).getOrder().getId());
        assertEquals("test-id2", result.get(1).getOrder().getId());
    }
}
