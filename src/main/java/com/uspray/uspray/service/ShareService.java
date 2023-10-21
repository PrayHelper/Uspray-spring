package com.uspray.uspray.service;

import com.uspray.uspray.DTO.sharedpray.request.SharedPrayRequestDto;
import com.uspray.uspray.DTO.sharedpray.response.SharedPrayResponseDto;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.domain.Pray;
import com.uspray.uspray.domain.SharedPray;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.CustomException;
import com.uspray.uspray.exception.model.NotFoundException;
import com.uspray.uspray.infrastructure.MemberRepository;
import com.uspray.uspray.infrastructure.PrayRepository;
import com.uspray.uspray.infrastructure.SharedPrayRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShareService {

    private final SharedPrayRepository sharedPrayRepository;
    private final MemberRepository memberRepository;
    private final PrayRepository prayRepository;

    @Transactional(readOnly = true)
    public List<SharedPrayResponseDto> getSharedPrayList(String userId) {
        Member member = memberRepository.getMemberByUserId(userId);
        List<SharedPray> sharedPrayList = sharedPrayRepository.findAllByMemberOrderByCreatedAtDesc(member);
        return sharedPrayList.stream()
            .map(SharedPrayResponseDto::of)
            .collect(Collectors.toList());

    }

    @Transactional
    public void sharePray(String userId, SharedPrayRequestDto sharedPrayRequestDto) {
        List<Member> receiverList = memberRepository.findAllByUserIdIn(sharedPrayRequestDto.getReceiverId());
        if (receiverList.size() != sharedPrayRequestDto.getReceiverId().size()) {
            throw new NotFoundException(ErrorStatus.NOT_FOUND_USER_EXCEPTION, ErrorStatus.NOT_FOUND_USER_EXCEPTION.getMessage());
        }

        List<Pray> prayList = prayRepository.findAllByIdIn(sharedPrayRequestDto.getPrayId());
        if (prayList.size() != sharedPrayRequestDto.getPrayId().size()) {
            throw new NotFoundException(ErrorStatus.PRAY_NOT_FOUND_EXCEPTION, ErrorStatus.PRAY_NOT_FOUND_EXCEPTION.getMessage());
        }

        for (Pray pray : prayList) {
            if (!pray.getMember().getUserId().equals(userId)) {
                throw new CustomException(ErrorStatus.SHARE_NOT_AUTHORIZED_EXCEPTION, ErrorStatus.SHARE_NOT_AUTHORIZED_EXCEPTION.getMessage());
            }
            if (pray.getDeleted()) {
                throw new CustomException(ErrorStatus.PRAY_ALREADY_DELETED_EXCEPTION, ErrorStatus.PRAY_ALREADY_DELETED_EXCEPTION.getMessage());
            }
        }

        for (Member receiver : receiverList) {
            if (receiver.getUserId().equals(userId)) {
                throw new CustomException(ErrorStatus.SENDER_RECEIVER_SAME_EXCEPTION, ErrorStatus.SENDER_RECEIVER_SAME_EXCEPTION.getMessage());
            }
            for (Pray pray : prayList) {
                SharedPray sharedPray = SharedPray.builder()
                    .member(receiver)
                    .pray(pray)
                    .build();
                sharedPrayRepository.save(sharedPray);
            }
        }
    }

    @Transactional
    public void deleteSharedPray(String userId, Long sharedPrayId) {

        Member member = memberRepository.getMemberByUserId(userId);
        if (!sharedPrayRepository.existsById(sharedPrayId)) {
            throw new NotFoundException(ErrorStatus.NOT_FOUND_SHARED_PRAY_EXCEPTION, ErrorStatus.NOT_FOUND_SHARED_PRAY_EXCEPTION.getMessage());
        }
        List<SharedPray> sharedPrayList = sharedPrayRepository.findAllByMemberOrderByCreatedAtDesc(member);

        // 본인 sharedPray가 아니면 지우지 못하게 막아야 함
        // 본인 sharedPray를 가지고 와서 아이디가 일치하면 삭제, 아니면 exception 발생
        for (SharedPray s : sharedPrayList) {
            if (Objects.equals(s.getId(), sharedPrayId)) {
                sharedPrayRepository.deleteById(sharedPrayId);
                return;
            }
        }
        throw new CustomException(ErrorStatus.DELETE_NOT_AUTHORIZED_EXCEPTION, ErrorStatus.DELETE_NOT_AUTHORIZED_EXCEPTION.getMessage());
    }

    @Transactional
    public void saveSharedPray(String userId, Long sharedPrayId) {
        Member member = memberRepository.getMemberByUserId(userId);
        SharedPray sharedPray = sharedPrayRepository.findById(sharedPrayId).orElseThrow(
            () -> new NotFoundException(ErrorStatus.NOT_FOUND_SHARED_PRAY_EXCEPTION, ErrorStatus.NOT_FOUND_SHARED_PRAY_EXCEPTION.getMessage()));
        if (sharedPray.getPray().getDeleted()) {
            sharedPrayRepository.deleteById(sharedPrayId);
            throw new CustomException(ErrorStatus.PRAY_ALREADY_DELETED_EXCEPTION, ErrorStatus.PRAY_ALREADY_DELETED_EXCEPTION.getMessage());
        }
        Pray pray = Pray.builder()
            .member(member)
            .content(sharedPray.getPray().getContent())
            .deadline(sharedPray.getPray().getDeadline())
            .originPrayId(sharedPray.getPray().getId())
            .build();
        prayRepository.save(pray);
        sharedPrayRepository.deleteById(sharedPrayId);
    }

    @Transactional
    public void cleanSharedPray(LocalDate threshold) {
        List<SharedPray> sharedPrayList = sharedPrayRepository.findAllByCreatedAtBefore(threshold);
        sharedPrayRepository.deleteAll(sharedPrayList);
    }
}
