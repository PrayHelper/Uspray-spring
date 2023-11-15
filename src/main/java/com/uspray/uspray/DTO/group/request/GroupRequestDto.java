package com.uspray.uspray.DTO.group.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupRequestDto {

        @NotNull(message = "모임 이름을 입력해주세요.")
        private String name;
}
