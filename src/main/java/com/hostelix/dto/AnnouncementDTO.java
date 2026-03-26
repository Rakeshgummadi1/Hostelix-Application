package com.hostelix.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnnouncementDTO {
    private String title;
    private String description;
    private String type;
    private String date;
}
