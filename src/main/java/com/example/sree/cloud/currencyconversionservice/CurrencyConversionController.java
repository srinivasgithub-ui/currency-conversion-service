package com.example.sree.cloud.currencyconversionservice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CurrencyConversionController {
	
	@Autowired
	private Environment environment;


	
	@Autowired
	private CurrencyExchangeServiceProxy Proxy;
	
	@GetMapping(path="/currency-converter/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrency(@PathVariable("from") String from,
			                                    @PathVariable("to") String to,
			                                    @PathVariable("quantity") BigDecimal quantity){
		
		CurrencyConversionBean CurrencyConversionBean=new CurrencyConversionBean(1000L,from,to,BigDecimal.ONE,quantity,quantity,Integer.parseInt(environment.getProperty("local.server.port")));
		
		return CurrencyConversionBean;
		
		
	}
	
	@GetMapping(path="/currency-converter-resttemplate/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrencyresttemplate(@PathVariable("from") String from,
			                                    @PathVariable("to") String to,
			                                    @PathVariable("quantity") BigDecimal quantity){
		
	    Map<String,String> uriVariables=new HashMap<>();
		uriVariables.put("from", from);
		uriVariables.put("to", to);
	ResponseEntity<CurrencyConversionBean>	responseEntity=new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}", 
			                                                CurrencyConversionBean.class, uriVariables);
	CurrencyConversionBean response=responseEntity.getBody();
		
		return new CurrencyConversionBean(response.getId(),from,to,response.getConversionMultiple(),quantity,
				quantity.multiply(response.getConversionMultiple()),response.getPort());
	}
	
	@GetMapping(path="/currency-converter-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrencyFeign(@PathVariable("from") String from,
			                                    @PathVariable("to") String to,
			                                    @PathVariable("quantity") BigDecimal quantity){
		
		CurrencyConversionBean response=Proxy.retriveExchangeValue(from, to);
		
		return new CurrencyConversionBean(response.getId(),from,to,response.getConversionMultiple(),quantity,quantity.multiply(response.getConversionMultiple()),response.getPort());
		
	}

}
