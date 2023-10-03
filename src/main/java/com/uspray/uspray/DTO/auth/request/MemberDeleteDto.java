package com.uspray.uspray.DTO.auth.request;

import com.uspray.uspray.Enums.DeleteReason;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
public class MemberDeleteDto {

    @Schema(example = "[DONT_NEED, USE_DIFFICULT]", type = "array")
    private List<DeleteReason> deleteReason;
    @Schema(example = "다음에 올게요")
    private String reason;

}
