package uk.gov.hmcts.reference.api.appeals;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.payment.api.contract.PaymentDto;
import uk.gov.hmcts.reference.api.appeals.Appeal.AppealType;
import uk.gov.hmcts.reference.api.appeals.Appeal.Payment;
import uk.gov.hmcts.reference.api.fees.FeesRegisterClient;
import uk.gov.hmcts.reference.api.payments.PaymentClient;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static uk.gov.hmcts.reference.api.appeals.Appeal.AppealStatus.AWAITING_PAYMENT;


@RestController
public class AppealController {
    private final AppealRepository appealRepository;
    private final PaymentClient paymentClient;
    private final FeesRegisterClient feesRegisterClient;

    @Autowired
    public AppealController(AppealRepository appealRepository, PaymentClient paymentClient, FeesRegisterClient feesRegisterClient) {
        this.appealRepository = appealRepository;
        this.paymentClient = paymentClient;
        this.feesRegisterClient = feesRegisterClient;
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

    @RequestMapping(value = "/users/{userId}/appeals/{appealId}", method = GET)
    public AppealDto retrieve(@PathVariable("userId") String userId,
                              @NotNull @PathVariable Integer appealId) {
        return toDto(appealRepository.retrieve(appealId));
    }

    private AppealDto toDto(Appeal appeal) {
        Optional<String> paymentUrl = appeal.getPaymentUrl(paymentFactory());

        return AppealDto.anAppealDtoWith()
                .id(appeal.getId())
                .type(appeal.getType())
                .status(appeal.getStatus())
                .description(appeal.getDescription())
                .paymentUrl(paymentUrl.orElse(null))
                .build();
    }

    private Function<AppealType, Payment> paymentFactory() {
        return (appealType) -> {
            Integer amount = feesRegisterClient.retrieve(appealType.getFeeCode());
            AtomicReference<PaymentDto> paymentDto = new AtomicReference<>(paymentClient.create(amount, "https://localhost:8080/"));

            return new Payment() {
                @Override
                public void refresh() {
                    paymentDto.set(paymentClient.retrieve(paymentDto.get().getId()));
                }

                @Override
                public boolean isActive() {
                    return !paymentDto.get().getState().getFinished();
                }

                @Override
                public boolean isPaid() {
                    return "success".equals(paymentDto.get().getState().getStatus());
                }

                @Override
                public String getUrl() {
                    return paymentDto.get().getLinks().getNextUrl().getHref();
                }
            };
        };
    }
}
