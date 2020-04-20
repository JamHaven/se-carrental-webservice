package pacApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pacApp.pacSoapConnector.SoapConvertCurrencyConnector;


@SpringBootApplication
public class WebServerMachine {

	@Bean
	public PasswordEncoder getPasswordEncoder(){
		return new BCryptPasswordEncoder();
	}

	public static void main(String... args) {
		SpringApplication.run(WebServerMachine.class, args);
		System.out.println(new SoapConvertCurrencyConnector().getCurrencyCodesResponse());
		System.out.println(new SoapConvertCurrencyConnector().convertCurrency(3,  "EUR","JPY"));

	}	 
}
