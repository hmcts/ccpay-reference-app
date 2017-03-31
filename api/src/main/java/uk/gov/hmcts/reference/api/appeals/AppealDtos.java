package uk.gov.hmcts.reference.api.appeals;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;
import uk.gov.hmcts.reference.api.appeals.Appeal.AppealStatus;
import uk.gov.hmcts.reference.api.appeals.Appeal.AppealType;

public class AppealDtos {

    @Data
    @Wither
    @Builder(builderMethodName = "anAppealDtoWith")
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppealDto {
        private Integer id;
        private AppealType type;
        private AppealStatus status;
        private String description;
        private String paymentUrl;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppealListItemDto {
        private Integer id;
        private AppealType type;
        private AppealStatus status;
        private String description;
    }
}
