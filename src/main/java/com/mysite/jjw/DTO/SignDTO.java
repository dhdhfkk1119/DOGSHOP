package com.mysite.jjw.DTO;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignDTO {
    @NotBlank(message = "아이디는 필수 입력값입니다.")
    private String id;        // 아이디

    @NotEmpty(message = "비밀번호는 필수 입력값입니다.")
    private String password;  // 비밀번호

    @NotEmpty(message = "비밀번호 확인을 입력해주세요.")
    private String repassword; // 비밀번호 확인

    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String name;      // 이름

    @NotBlank(message = "성별을 선택해주세요")
    private String sex;       // 성별

    @NotBlank(message = "전화번호는 필수 입력값입니다.")
    private String phone;     // 전화번호

    @NotEmpty(message = "이메일을 입력하세요.")
    private String emailLocal; // 이메일 로컬 (ex: user)

    @NotBlank(message = "이메일 도메인을 선택하거나 입력하세요.")
    private String emailDomain; // 이메일 도메인 (ex: naver.com)

    public String getEmail() {
        if (emailLocal != null && !emailLocal.isEmpty() && emailDomain != null && !emailDomain.isEmpty()) {
            return emailLocal + "@" + emailDomain;
        }
        return "";
    }
    // year, month, day로부터 LocalDate를 생성
    @NotBlank(message = "연도를 입력하세요.")
    private String year;  // 연도

    @NotBlank(message = "월을 입력하세요.")
    private String month; // 월

    @NotBlank(message = "일을 입력하세요.")
    private String day;   // 일

    public LocalDate getAge() {
        return LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
    }

    private String memberImage = "basic.png"; // 이미지

}
