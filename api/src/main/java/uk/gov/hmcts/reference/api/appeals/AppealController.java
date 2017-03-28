package uk.gov.hmcts.reference.api.appeals;

import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reference.api.appeals.Appeal.AppealType;
import uk.gov.hmcts.reference.api.appeals.AppealDtos.AppealDto;
import uk.gov.hmcts.reference.api.appeals.AppealDtos.AppealListItemDto;

import static java.util.stream.Collectors.toList;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static uk.gov.hmcts.reference.api.appeals.Appeal.AppealStatus.AWAITING_PAYMENT;


@RestController
public class AppealController {
    private final AppealRepository appealRepository;
    private final PaymentFactory paymentFactory;

    @Autowired
    public AppealController(AppealRepository appealRepository, PaymentFactory paymentFactory) {
        this.appealRepository = appealRepository;
        this.paymentFactory = paymentFactory;
    }

    @RequestMapping(value = "/users/{userId}/appeals", method = POST)
    public AppealDto create(@PathVariable("userId") String userId,
                            @NotEmpty @RequestParam AppealType type,
                            @NotEmpty @RequestParam String description) {

        return toDto(appealRepository.createOrUpdate(Appeal.anAppealWith()
                .type(type)
                .status(AWAITING_PAYMENT)
                .description(description)
                .build()
        ));
    }

    @RequestMapping(value = "/users/{userId}/appeals", method = GET)
    public List<AppealListItemDto> retrieve(@PathVariable("userId") String userId) {
        return appealRepository.retrieveAll().stream()
                .map((appeal) -> new AppealListItemDto(appeal.getId(), appeal.getType(), appeal.getDescription()))
                .collect(toList());
    }

    @RequestMapping(value = "/users/{userId}/appeals/{appealId}", method = GET)
    public AppealDto retrieve(@PathVariable("userId") String userId,
                              @NotNull @PathVariable Integer appealId) {
        return toDto(appealRepository.retrieve(appealId));
    }

    private AppealDto toDto(Appeal appeal) {
        Optional<String> paymentUrl = appeal.getPaymentUrl(paymentFactory);

        return AppealDto.anAppealDtoWith()
                .id(appeal.getId())
                .type(appeal.getType())
                .status(appeal.getStatus())
                .description(appeal.getDescription())
                .paymentUrl(paymentUrl.orElse(null))
                .build();
    }

}
