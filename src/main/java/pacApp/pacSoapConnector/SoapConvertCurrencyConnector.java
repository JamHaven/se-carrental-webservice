package pacApp.pacSoapConnector;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import com.consumingwebservice.wsdl.ConvertCurrency;
import com.consumingwebservice.wsdl.ConvertCurrencyResponse;
import com.consumingwebservice.wsdl.GetCurrencyCodes;
import com.consumingwebservice.wsdl.GetCurrencyCodesResponse;
import com.consumingwebservice.wsdl.ObjectFactory;

import pacApp.pacSoapConnector.context.SoapMarshaller;

public class SoapConvertCurrencyConnector extends WebServiceGatewaySupport {
	
	private void setupMarshaller() {
		getWebServiceTemplate().setMarshaller(new SoapMarshaller().marshaller());
		getWebServiceTemplate().setUnmarshaller(new SoapMarshaller().marshaller());		
	}
	
	public GetCurrencyCodesResponse getCurrencyCodesResponse() {
		try {
			GetCurrencyCodes request = new GetCurrencyCodes();
			setupMarshaller();
			GetCurrencyCodesResponse response = (GetCurrencyCodesResponse) getWebServiceTemplate()
		        .marshalSendAndReceive("http://localhost:50923/Service1.svc/soap", request,
		            new SoapActionCallback("http://tempuri.org/IService1/GetCurrencyCodes"));
			System.out.println("Result: "+response.getGetCurrencyCodesResult().getValue().getString());
			return response;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ConvertCurrencyResponse convertCurrency() {
		try {
			ConvertCurrency request = new ConvertCurrency();
			setupMarshaller();
			ObjectFactory factory = new ObjectFactory();
			request.setValue(factory.createConvertCurrencyValue("1"));
			request.setToCurrency(factory.createConvertCurrencyToCurrency("JPY"));
			ConvertCurrencyResponse response = (ConvertCurrencyResponse)getWebServiceTemplate()
					 .marshalSendAndReceive("http://localhost:50923/Service1.svc/soap", request,
							  new SoapActionCallback("http://tempuri.org/IService1/ConvertCurrency"));
			System.out.println("Result: "+response.getConvertCurrencyResult().getValue());	
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
