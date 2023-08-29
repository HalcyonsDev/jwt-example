package com.halcyon.jwt_learning.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {

    @Email(message = "Email is not valid")
    private String email;

    @Size(min = 6, message = "Password should be more that 6 letters!")
    private String password;

    @Size(min = 1, max = 32, message = "First name should be more that 1 letter and lass than 32 letters!")
    private String firstname;

    @Size(min = 1, max = 32, message = "Last name should be more than 1 letter and less than 32 letters!")
    private String lastname;

}
