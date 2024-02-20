package com.uspray.uspray.DTO.sharedpray.response;

import com.uspray.uspray.domain.Pray;
import com.uspray.uspray.domain.SharedPray;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공유기도제목 응답 DTO")
public class SharedPrayResponseDto {

    @Schema(description = "공유기도제목 ID", example = "1")
    private Long sharedPrayId;

    @Schema(description = "기도제목 ID", example = "1")
    private Long prayId;

    @Schema(description = "기도제목 작성자 ID", example = "홍길동")
    private String userId;

    @NotNull
    @Schema(description = "기도제목 내용", example = "@@이가 $$ 할 수 있도록")
    private String content;

    @NotNull
    @Schema(description = "기도제목 마감일", example = "2025-01-01")
    private LocalDate deadline;

    @Schema(description = "기도제목 생성일", example = "2021-01-01 00:00:00")
    private LocalDateTime createdAt;

    public static SharedPrayResponseDto of(SharedPray sharedPray) {
        Pray pray = sharedPray.getPray();

        return new SharedPrayResponseDto(sharedPray.getId(), sharedPray.getPray().getId(),
            sharedPray.getPray().getMember().getUserId(),
            sharedPray.getPray().getContent(), sharedPray.getPray().getDeadline(),
            sharedPray.getCreatedAt());
    }
}
