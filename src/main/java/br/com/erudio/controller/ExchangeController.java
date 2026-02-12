package br.com.erudio.controller;

import br.com.erudio.environment.InstanceInformationService;
import br.com.erudio.model.Exchange;
import br.com.erudio.repository.ExchangeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Tag(name="Exchange Endpoint")
@RestController
@RequestMapping("exchange-service")
public class ExchangeController {
    @Autowired
    InstanceInformationService info;

    @Autowired
    ExchangeRepository repository;

    private final Logger logger = LoggerFactory.getLogger(ExchangeController.class);
/*
    @GetMapping(value = "/{amount}/{from}/{to}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Exchange getExchange(
            @PathVariable("amount") BigDecimal amount,
            @PathVariable("from") String from,
            @PathVariable("to") String to) {
        return new Exchange(1L, from, to, BigDecimal.ONE, BigDecimal.ONE,
                "PORT " + info.retrieveServerPort());
    }
*/

    @Operation(summary = "Get a conversion amount from currency")
    @GetMapping(value = "/{amount}/{from}/{to}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Exchange getExchange(
            @PathVariable("amount") BigDecimal amount,
            @PathVariable("from") String from,
            @PathVariable("to") String to) {

        logger.info("getExchanged is called with -> {}, {}, {}",amount, from, to);

        Exchange exchange = repository.findByFromAndTo(from, to);

        if (exchange == null) {
            throw new RuntimeException("Currency unsupported");
        }
        BigDecimal conversionFactor = exchange.getConversionFactor();
        BigDecimal convertedValue = conversionFactor.multiply(amount);

        //Ideal é ter um DTO, mas foi simplificado em Transient
        exchange.setConvertedValue(convertedValue);
        exchange.setEnvironment("PORT " + info.retrieveServerPort());
        return exchange;
    }

}
