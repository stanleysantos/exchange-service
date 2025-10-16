package br.com.erudio.controller;

import br.com.erudio.environment.InstanceInformationService;
import br.com.erudio.model.Exchange;
import br.com.erudio.repository.ExchangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("exchange-service")
public class ExchangeController {
    @Autowired
    InstanceInformationService info;

    @Autowired
    ExchangeRepository repository;
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

    @GetMapping(value = "/{amount}/{from}/{to}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Exchange getExchange(
            @PathVariable("amount") BigDecimal amount,
            @PathVariable("from") String from,
            @PathVariable("to") String to) {
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
