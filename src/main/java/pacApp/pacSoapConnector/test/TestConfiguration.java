package pacApp.pacSoapConnector.test;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import com.consumingwebservice.wsdl.GetCurrencyCodesResponse;

import pacApp.pacSoapConnector.SoapConvertCurrencyConnector;

@Configuration
public class TestConfiguration {
	//@Bean
	public CommandLineRunner lookup(SoapConvertCurrencyConnector quoteClient) {
	    return args -> {
	      GetCurrencyCodesResponse response = quoteClient.getCurrencyCodesResponse();
	      System.out.println("Result: "+response.getGetCurrencyCodesResult().getValue().getString());
	    };
	}
}
