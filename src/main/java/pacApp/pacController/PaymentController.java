package pacApp.pacController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pacApp.pacData.RentalRepository;
import pacApp.pacData.UserRepository;
import pacApp.pacModel.Rental;
import pacApp.pacModel.User;
import pacApp.pacModel.request.PayInfo;
import pacApp.pacModel.response.GenericResponse;
import pacApp.pacSecurity.JwtAuthenticatedProfile;
import pacApp.pacSoapConnector.SoapConvertCurrencyConnector;

import java.util.List;
import java.util.Optional;

@RestController
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    /*
    @Autowired

    @Bean
    SoapConvertCurrencyConnector currencyConnector() {
        return new SoapConvertCurrencyConnector(Jaxb2Marshaller marshaller);
    } */

    public PaymentController(UserRepository userRepository, RentalRepository rentalRepository) {
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
    }

    @RequestMapping(value = "/currencies", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getCurrencyCodes() {
        List<String> currencyCodeList = null;

        try {
            SoapConvertCurrencyConnector currencyConnector = new SoapConvertCurrencyConnector();
            currencyCodeList = currencyConnector.getCurrencyCodesResponse();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

        if (currencyCodeList == null) {
            GenericResponse response = new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Currency conversion unavailable");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        currencyCodeList.add("EUR");

        return new ResponseEntity<>(currencyCodeList, HttpStatus.OK);
    }

    @RequestMapping(value = "/pay/info", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity payInfo(@RequestBody PayInfo payInfo) {
        if (payInfo == null) {
            GenericResponse response = new GenericResponse(HttpStatus.BAD_REQUEST.value(), "Missing request body");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        //check for input price

        if (payInfo.getInputPrice() == null) {
            GenericResponse response = new GenericResponse(HttpStatus.BAD_REQUEST.value(), "Missing input price");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        SoapConvertCurrencyConnector currencyConnector = null;
        List<String> currencyCodeList = null;

        try {
            currencyConnector = new SoapConvertCurrencyConnector();
            currencyCodeList = currencyConnector.getCurrencyCodesResponse();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

        if (currencyCodeList == null) {
            GenericResponse response = new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Currency conversion unavailable");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        currencyCodeList.add("EUR");

        String inputCurrency = payInfo.getInputCurrency();
        String outputCurrency = payInfo.getOutputCurrency();

        if (!currencyCodeList.contains(inputCurrency)) {
            GenericResponse response = new GenericResponse(HttpStatus.BAD_REQUEST.value(),"Invalid input currency " + inputCurrency);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (!currencyCodeList.contains(outputCurrency)) {
            GenericResponse response = new GenericResponse(HttpStatus.BAD_REQUEST.value(),"Invalid output currency " + outputCurrency);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Float conversionResponse = null;

        Float value = Float.valueOf(payInfo.getInputPrice().floatValue());

        try {
            conversionResponse = currencyConnector.convertCurrency(value, inputCurrency, outputCurrency);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

        if (conversionResponse == null) {
            GenericResponse response = new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Currency conversion error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        log.info(conversionResponse.toString());
        Double convertedPrice = Double.valueOf(conversionResponse);
        log.info(convertedPrice.toString());

        payInfo.setOutputPrice(convertedPrice);

        return new ResponseEntity<>(payInfo, HttpStatus.OK);
    }

    @RequestMapping(value = "/pay", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity pay(@RequestBody PayInfo payInfo) {
        if (payInfo == null) {
            GenericResponse response = new GenericResponse(HttpStatus.BAD_REQUEST.value(), "Missing request body");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth instanceof JwtAuthenticatedProfile)) {
            GenericResponse response = new GenericResponse(403, "Authentication failure");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        JwtAuthenticatedProfile authenticatedProfile = (JwtAuthenticatedProfile) auth;
        String userEmail = authenticatedProfile.getName();

        Optional<User> optUser = this.userRepository.findOneByEmail(userEmail);

        if (!optUser.isPresent()) {
            GenericResponse response = new GenericResponse(HttpStatus.BAD_REQUEST.value(), "Invalid user");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        User user = optUser.get();

        //check for rental id

        if (payInfo.getRentalId() == null) {
            GenericResponse response = new GenericResponse(HttpStatus.BAD_REQUEST.value(), "Missing rental id");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Long rentalId = payInfo.getRentalId();

        Optional<Rental> optionalRental = this.rentalRepository.findById(rentalId);

        if (!optionalRental.isPresent()) {
            GenericResponse response = new GenericResponse(HttpStatus.BAD_REQUEST.value(),"Rental not found");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Rental rental = optionalRental.get();

        //check rental user and request user

        if (rental.getUser().getId() != user.getId()) {
            GenericResponse response = new GenericResponse(HttpStatus.FORBIDDEN.value(),"Rental payment forbidden");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        SoapConvertCurrencyConnector currencyConnector = null;
        List<String> currencyCodeList = null;

        try {
            currencyConnector = new SoapConvertCurrencyConnector();
            currencyCodeList = currencyConnector.getCurrencyCodesResponse();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

        if (currencyCodeList == null) {
            GenericResponse response = new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Currency conversion unavailable");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        currencyCodeList.add("EUR");

        String inputCurrency = payInfo.getInputCurrency();
        String outputCurrency = payInfo.getOutputCurrency();

        if (!currencyCodeList.contains(inputCurrency)) {
            GenericResponse response = new GenericResponse(HttpStatus.BAD_REQUEST.value(),"Invalid input currency " + inputCurrency);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (!currencyCodeList.contains(outputCurrency)) {
            GenericResponse response = new GenericResponse(HttpStatus.BAD_REQUEST.value(),"Invalid output currency " + outputCurrency);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (rental.getPrice() == null) {
            GenericResponse response = new GenericResponse(HttpStatus.BAD_REQUEST.value(),"Rental not finished");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Float value = Float.valueOf(rental.getPrice().floatValue());
        Float convertedValue = null;

        try {
            convertedValue = currencyConnector.convertCurrency(value, "USD", inputCurrency);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

        if (convertedValue == null) {
            GenericResponse response = new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Currency conversion error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            convertedValue = currencyConnector.convertCurrency(convertedValue, inputCurrency, outputCurrency);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

        if (convertedValue == null) {
            GenericResponse response = new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Currency conversion error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        log.info(convertedValue.toString());
        Double convertedPrice = Double.valueOf(convertedValue);
        log.info(convertedPrice.toString());

        payInfo.setOutputPrice(convertedPrice);

        return new ResponseEntity<>(payInfo, HttpStatus.OK);
    }
}
