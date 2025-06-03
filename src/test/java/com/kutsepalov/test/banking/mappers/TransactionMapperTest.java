package com.kutsepalov.test.banking.mappers;

import com.kutsepalov.test.banking.dtos.transaction.TransactionDto;
import com.kutsepalov.test.banking.dtos.transaction.TransactionRequestDto;
import com.kutsepalov.test.banking.dtos.transaction.TransactionType;
import com.kutsepalov.test.banking.entities.Transaction;
import com.kutsepalov.test.banking.util.TimeUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class TransactionMapperTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private final TransactionMapper transactionMapper = Mappers.getMapper(TransactionMapper.class);

    @Test
    void entityToDto_mapsFieldsCorrectly() {
        Transaction entity = new Transaction();
        entity.setId(UUID.randomUUID());
        entity.setSourceIban("SRC123");
        entity.setTargetIban("TGT456");
        entity.setAmount(BigDecimal.TEN);
        entity.setType(TransactionType.TRANSFER);
        entity.setCurrency("EUR");
        entity.setTransactionDate(TimeUtil.now());

        TransactionDto dto = transactionMapper.entityToDto(entity);

        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getSourceIban(), dto.getSourceIban());
        assertEquals(entity.getTargetIban(), dto.getTargetIban());
        assertEquals(entity.getAmount(), dto.getAmount());
        assertEquals(entity.getType(), dto.getType());
        assertEquals(entity.getCurrency(), dto.getCurrency());
    }

    @Test
    void requestToDto_mapsFieldsCorrectly() {
        TransactionRequestDto request = new TransactionRequestDto();
        request.setSourceIban("SRC789");
        request.setTargetIban("TGT987");
        request.setAmount(BigDecimal.ONE);
        request.setCurrency("USD");

        TransactionDto dto = transactionMapper.requestToDto(request, TransactionType.DEPOSIT);

        assertNull(dto.getId());
        assertEquals(request.getSourceIban(), dto.getSourceIban());
        assertEquals(request.getTargetIban(), dto.getTargetIban());
        assertEquals(request.getAmount(), dto.getAmount());
        assertEquals(TransactionType.DEPOSIT, dto.getType());
        assertEquals(request.getCurrency(), dto.getCurrency());
    }

    @Test
    void dtoToEntity_mapsFieldsCorrectly() {
        TransactionDto dto = new TransactionDto();
        dto.setId(UUID.randomUUID());
        dto.setSourceIban("SRC111");
        dto.setTargetIban("TGT222");
        dto.setAmount(BigDecimal.valueOf(50));
        dto.setType(TransactionType.WITHDRAW);
        dto.setCurrency("GBP");
        dto.setTransactionDate(TimeUtil.nowUTC());

        Transaction entity = transactionMapper.dtoToEntity(dto);

        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getSourceIban(), entity.getSourceIban());
        assertEquals(dto.getTargetIban(), entity.getTargetIban());
        assertEquals(dto.getAmount(), entity.getAmount());
        assertEquals(dto.getType(), entity.getType());
        assertEquals(dto.getCurrency(), entity.getCurrency());
    }
}