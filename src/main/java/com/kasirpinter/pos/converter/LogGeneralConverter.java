package com.kasirpinter.pos.converter;

import com.kasirpinter.pos.entity.LogHistory;
import com.kasirpinter.pos.model.LogGeneralRequest;
import com.kasirpinter.pos.repository.LogHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogGeneralConverter {

    private final LogHistoryRepository logHistoryRepository;

    public void sendLogHistory(LogGeneralRequest dto){
        LogHistory newData = new LogHistory();
        newData.setIdentifier(dto.getIdentifier());
        newData.setFromLog(dto.getFromLog());
        newData.setToLog(dto.getToLog());
        newData.setNote(dto.getNote());
        newData.setActionBy(dto.getActionBy());
        logHistoryRepository.save(newData);
    }

}
