/* @MENTEE_POWER (C)2026 */
package ru.mentee.banking.validation.domain.model;

import java.time.LocalDate;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalInfo {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String passport;
    private String phone;
    private String email;
}
