package com.future.pms.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder public class ListLevelRequest {
    private String idLevel;
    private String levelName;
    private String levelStatus;
}
