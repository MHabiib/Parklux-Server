package com.future.pms.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder public class SectionDetailRequest {
    private String idSection;
    private String sectionName;
    private Integer totalEmptySlot = 0;
    private Integer totalTakenSlot = 0;
    private Integer totalDisableSlot = 0;
    private String status;
}
