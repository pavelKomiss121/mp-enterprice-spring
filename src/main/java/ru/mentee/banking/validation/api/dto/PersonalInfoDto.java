/* @MENTEE_POWER (C)2026 */
package ru.mentee.banking.validation.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalInfoDto {

    @NotBlank(message = "{validation.personalInfo.firstName.notBlank}")
    @Size(min = 2, max = 50, message = "{validation.personalInfo.firstName.size}")
    private String firstName;

    @NotBlank(message = "{validation.personalInfo.lastName.notBlank}")
    @Size(min = 2, max = 50, message = "{validation.personalInfo.lastName.size}")
    private String lastName;

    @NotNull(message = "{validation.personalInfo.birthDate.notNull}") @Past(message = "{validation.personalInfo.birthDate.past}")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @NotBlank(message = "{validation.personalInfo.passport.notBlank}")
    @Pattern(regexp = "^\\d{4} \\d{6}$", message = "{validation.personalInfo.passport.pattern}")
    private String passport;

    @NotBlank(message = "{validation.personalInfo.phone.notBlank}")
    private String phone;

    @NotBlank(message = "{validation.personalInfo.email.notBlank}")
    @Email(message = "{validation.personalInfo.email.valid}")
    private String email;
}
