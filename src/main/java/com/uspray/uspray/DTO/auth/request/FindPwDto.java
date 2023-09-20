package com.uspray.uspray.DTO.auth.request;

import lombok.Data;

@Data
public class FindPwDto {

    String userId;
    String name;
    String phone;
    String password;

}
