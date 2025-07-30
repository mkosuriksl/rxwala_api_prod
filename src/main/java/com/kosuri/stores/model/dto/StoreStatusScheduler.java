package com.kosuri.stores.model.dto;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kosuri.stores.dao.StoreEntity;
import com.kosuri.stores.dao.StoreRepository;

@Component
public class StoreStatusScheduler {

    private final StoreRepository storeRepository;

    public StoreStatusScheduler(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Scheduled(cron = "0 0 0 * * *") // Runs daily at 12:00 AM
    @Transactional
    public void updateStoreStatusOnExpiry() {
        LocalDate today = LocalDate.now();
        List<StoreEntity> expiredStores = storeRepository.findByExpiryDate(today.toString());

        for (StoreEntity store : expiredStores) {
            store.setStatus("Inactive");
        }

        storeRepository.saveAll(expiredStores);
    }

}

