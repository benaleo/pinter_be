package com.kasirpinter.pos.converter;

import com.kasirpinter.pos.entity.LogHistory;
import com.kasirpinter.pos.entity.RlUserShift;
import com.kasirpinter.pos.entity.Users;
import com.kasirpinter.pos.model.LogGeneralRequest;
import com.kasirpinter.pos.repository.LogHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogGeneralConverter {

    private final LogHistoryRepository logHistoryRepository;

    @Async
    public void sendLogHistory(LogGeneralRequest dto){
        LogHistory newData = new LogHistory();
        newData.setIdentifier(dto.getIdentifier());
        newData.setModel(dto.getModel());
        newData.setFromLog(dto.getFromLog());
        newData.setToLog(dto.getToLog());
        newData.setNote(dto.getNote());
        newData.setActionBy(dto.getActionBy());
        logHistoryRepository.save(newData);
    }

    public void sendLogHistoryPassword(Users user, String model) {
        sendLogHistory(new LogGeneralRequest(
                user.getSecureId(),
                model,
                "PASSWORD",
                "PASSWORD-UPDATE",
                user.getEmail() + " has changed the password.",
                "USER"
        ));
    }

    public void sendLogHistoryAvatar(Users user, String model, String from, String to) {
        sendLogHistory(new LogGeneralRequest(
                user.getSecureId(),
                model,
                from,
                to,
                user.getEmail() + " has + " + (to.contains("REMOVED") ? "removed" : "updated") + " the password.",
                "USER"
        ));
    }

    public void sendLogHistoryUpdateProfile(Users user, String model, String from, String to, String jsonResponse) {
        sendLogHistory(new LogGeneralRequest(
                user.getSecureId(),
                model,
                from,
                to,
                jsonResponse,
                "USER"
        ));
    }

    public void sendUpdateCompanyModal(Users user, RlUserShift userShift, Integer value) {
        sendLogHistory(new LogGeneralRequest(
                user.getSecureId(),
                "DASHBOARD",
                null,
                "MODAL-UPDATED",
                user.getEmail() + " has been updated cash to " + value + " in shift data " + userShift.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                "USER"
        ));
    }

    public void sendHistoryPresence(Users user, String type) {
        sendLogHistory(new LogGeneralRequest(
                user.getSecureId(),
                "DASHBOARD",
                type.equals("IN") ? null : "IN",
                type,
                user.getEmail() + " has been presence " + type,
                "USER"
        ));
    }
}
