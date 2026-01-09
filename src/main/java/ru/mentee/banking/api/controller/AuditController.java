/* @MENTEE_POWER (C)2025 */
package ru.mentee.banking.api.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mentee.banking.annotation.RequiresRole;
import ru.mentee.banking.api.dto.AuditEntryDto;
import ru.mentee.banking.domain.model.AuditEntry;
import ru.mentee.banking.service.AuditService;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping("/operations")
    @RequiresRole({"ADMIN"})
    public ResponseEntity<List<AuditEntryDto>> getAuditEntries(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    LocalDateTime to) {

        List<AuditEntry> entries;

        if (userId != null) {
            entries = auditService.findByUserId(userId);
        } else if (from != null && to != null) {
            entries = auditService.findByDateRange(from, to);
        } else {
            entries = auditService.findAll();
        }

        List<AuditEntryDto> dtos = entries.stream().map(this::toDto).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    private AuditEntryDto toDto(AuditEntry entry) {
        return AuditEntryDto.builder()
                .id(entry.getId())
                .userId(entry.getUserId())
                .operation(entry.getOperation())
                .timestamp(entry.getTimestamp())
                .status(entry.getStatus())
                .details(entry.getDetails())
                .build();
    }
}
