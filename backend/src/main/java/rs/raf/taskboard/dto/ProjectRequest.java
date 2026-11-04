package rs.raf.taskboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Isti podaci za kreiranje i izmenu, pa nema potrebe za dve klase
public record ProjectRequest(
        @NotBlank @Size(max = 200) String name,
        String description
) {
}