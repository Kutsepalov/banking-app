package com.kutsepalov.test.banking.mappers;

import com.kutsepalov.test.banking.dtos.transaction.TransactionDto;
import com.kutsepalov.test.banking.dtos.transaction.TransactionRequestDto;
import com.kutsepalov.test.banking.dtos.transaction.TransactionType;
import com.kutsepalov.test.banking.entities.Transaction;
import com.kutsepalov.test.banking.util.TimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {UserMapper.class, TimeUtil.class}
)
public interface TransactionMapper {

    TransactionDto entityToDto(Transaction entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sourceIban", source = "request.sourceIban")
    @Mapping(target = "targetIban", source = "request.targetIban")
    @Mapping(target = "amount", source = "request.amount")
    @Mapping(target = "type", source = "operation")
    @Mapping(target = "currency", source = "request.currency")
    @Mapping(target = "transactionDate", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "sourceAccount", ignore = true)
    @Mapping(target = "targetAccount", ignore = true)
    TransactionDto requestToDto(TransactionRequestDto request, TransactionType operation);

    Transaction dtoToEntity(TransactionDto transactionDto);
}
