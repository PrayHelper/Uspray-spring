package com.uspray.uspray.DTO.group.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupRequestDto {

        @NotBlank(message = "모임 이름을 입력해주세요.")
        @Size(min = 1, max = 15, message = "모임 이름은 공백 포함 15자 이내로 입력해주세요.")
        private String name;
}
