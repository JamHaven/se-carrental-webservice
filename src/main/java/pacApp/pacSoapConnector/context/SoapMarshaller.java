package pacApp.pacSoapConnector.context;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import pacApp.pacSoapConnector.SoapConvertCurrencyConnector;

@Configuration
public class SoapMarshaller {
	@Bean
	public Jaxb2Marshaller marshaller() {
	  	Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
	    marshaller.setContextPath("com.consumingwebservice.wsdl");
	    return marshaller;
	}
}
