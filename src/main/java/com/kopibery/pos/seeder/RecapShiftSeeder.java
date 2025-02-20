package com.kopibery.pos.seeder;

import com.kopibery.pos.entity.MsShift;
import com.kopibery.pos.entity.ShiftRecap;
import com.kopibery.pos.repository.ShiftRecapRepository;
import com.kopibery.pos.repository.UserShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class RecapShiftSeeder {
    @Autowired
    private UserShiftRepository userShiftRepository;

    @Autowired
    private ShiftRecapRepository shiftRecapRepository;

    @Scheduled(cron = "0 0 0 * * ?") // Runs every day at midnight
    public void createShiftRecaps() {
        List<MsShift> activeShifts = userShiftRepository.findAllByIsActiveTrue();
        LocalDate today = LocalDate.now();

        for (MsShift shift : activeShifts) {
            ShiftRecap recap = new ShiftRecap();
            recap.setShift(shift);
            recap.setDate(today);
            // Set other necessary fields
            shiftRecapRepository.save(recap);
        }
    }
}
