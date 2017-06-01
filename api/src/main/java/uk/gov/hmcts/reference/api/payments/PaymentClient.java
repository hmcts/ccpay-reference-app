package uk.gov.hmcts.reference.api.payments;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.auth.checker.spring.useronly.UserDetails;
import uk.gov.hmcts.auth.provider.service.token.ServiceTokenGenerator;
import uk.gov.hmcts.payment.api.contract.CreatePaymentRequestDto;
import uk.gov.hmcts.payment.api.contract.PaymentDto;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static uk.gov.hmcts.payment.api.contract.CreatePaymentRequestDto.createPaymentRequestDtoWith;

@Component
public class PaymentClient {
    private final ServiceTokenGenerator serviceTokenGenerator;
    private final RestTemplate restTemplate;
    private final String baseUrl;

    @Autowired
    public PaymentClient(RestTemplate restTemplate,
                         @Value("${payment.client.baseUrl}") String baseUrl,
                         @Qualifier("cachedServiceTokenGenerator") ServiceTokenGenerator serviceTokenGenerator) {
        this.serviceTokenGenerator = serviceTokenGenerator;
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public PaymentDto create(Integer amount, String returnUrl) {
        HttpEntity<CreatePaymentRequestDto> requestEntity = new HttpEntity<>(
                createPaymentRequestDtoWith()
                        .reference(UUID.randomUUID().toString())
                        .description("Payment for appeal")
                        .amount(amount)
                        .returnUrl(returnUrl)
                        .build(),

                authorizationHeaders()
        );

        return restTemplate
                .exchange(baseUrl + "/users/{userId}/payments", POST, requestEntity, PaymentDto.class, currentUser().getUsername())
                .getBody();
    }

    public PaymentDto retrieve(String paymentId) {
        HttpEntity<CreatePaymentRequestDto> requestEntity = new HttpEntity<>(authorizationHeaders());

        return restTemplate
                .exchange(baseUrl + "/users/{userId}/payments/{paymentId}", GET, requestEntity, PaymentDto.class, currentUser().getUsername(), paymentId)
                .getBody();
    }

    private HttpHeaders authorizationHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", currentUser().getPassword());
        headers.add("ServiceAuthorization", serviceTokenGenerator.generate());
        return headers;
    }

    private UserDetails currentUser() {
        return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
