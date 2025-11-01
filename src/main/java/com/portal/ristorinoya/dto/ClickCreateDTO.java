package com.portal.ristorinoya.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ClickCreateDTO {
    @NotNull private Long promotionId;
}
