package com.uspray.uspray.service;

import com.uspray.uspray.domain.Pray;
import com.uspray.uspray.domain.SharedPray;
import com.uspray.uspray.infrastructure.SharedPrayRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShareService {

    private final SharedPrayRepository sharedPrayRepository;

    @Transactional
    public void cleanSharedPray(LocalDate threshold) {
        List<SharedPray> sharedPrayList = sharedPrayRepository.findAllByCreatedAtBefore(threshold);
        sharedPrayRepository.deleteAllInBatch(sharedPrayList);
    }

    @Transactional
    public void deleteByOriginPray(Pray pray) {
        List<SharedPray> sharedPrays = sharedPrayRepository.findAllByPray(pray);
        sharedPrayRepository.deleteAllInBatch(sharedPrays);
    }
}
