package com.uspray.uspray.service;

import com.uspray.uspray.DTO.sharedpray.response.SharedPrayListResponseDto;
import com.uspray.uspray.domain.SharedPray;
import com.uspray.uspray.infrastructure.SharedPrayRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShareService {

    private final SharedPrayRepository sharedPrayRepository;

    public SharedPrayListResponseDto getSharedPrayList(String username) {
        List<SharedPray> sharedPrayList = sharedPrayRepository.findAllByUserId(username);

    }
}
