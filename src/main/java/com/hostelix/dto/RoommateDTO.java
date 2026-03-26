package com.hostelix.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoommateDTO {
    private String name;
    private String id;
}