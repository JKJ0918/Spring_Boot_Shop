package org.zerock.shop.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

@Getter @Setter
public class MemberFormDto {

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotEmpty(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식으로 입력해주세요.")
    private String email;

    @NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
    @Length(min = 8, max = 16, message = "비밀번호는 8자 이상, 16자 이하로 입력해주세요")
    private String password;

    @NotEmpty(message = "주소는 필수 입력 값입니다.")
    private String address;

    // implementation group: 'org.springframework.boot', name: 'spring-boot-starter-validation', version: '3.2.4' 필수
    // @NotEmpty : null 체크 및 문자열의 경우 길이 0 인지
    // @NotBlank : null 체크 및 문자열의 경우 길이 0 및 빈 문자열 "" 인지 검사
    // @Length(min= , max= ) : 최소, 최대 길이 검사
    // @Email : 이메일 형식인지
    // @Max(숫자) : 지정한 값보다 작은지, @Min(숫자) : 지정한 값보다 큰지
    // @Null : 값이 null, @NotNull : 값이 Null이 아닌지
    // 컨트롤러에 @Valid를 사용한다.
}