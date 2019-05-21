package br.com.restSpring.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import aj.org.objectweb.asm.TypeReference;
import br.com.restSpring.dto.Cidade;
import br.com.restSpring.dto.Clima;
import br.com.restSpring.dto.ClimaConsolidado;
import br.com.restSpring.dto.IP;
import br.com.restSpring.dto.PessoaDTO;
import br.com.restSpring.response.Response;
import br.com.restSpring.service.PessoaService;



@RestController
@RequestMapping("/servico")
public class Controller {
	
	@Autowired
	PessoaService service;
	
	@PostMapping(path = "/cadastrar")
	public ResponseEntity<Response> cadastrar(@Valid @RequestBody PessoaDTO pessoaDTO) {
		
		try {
			
			InetAddress inetAddress = InetAddress.getLocalHost();
	        System.out.println("IP Address:- " + inetAddress.getHostAddress());
			
	        ObjectMapper mapper = new ObjectMapper();
	        
			String jSon = chamarServico("https://ipvigilante.com/189.40.89.168");
			
			IP ipVigilante = mapper.readValue(jSon, IP.class);
			
			pessoaDTO.setLatitude(ipVigilante.getData().getLatitude());
			pessoaDTO.setLongitude(ipVigilante.getData().getLongitude());
						
			jSon = chamarServico("https://www.metaweather.com/api/location/search/?lattlong="+ipVigilante.getData().getLatitude()+","+ipVigilante.getData().getLongitude());
			
			List<Cidade> listaCidades = Arrays.asList(mapper.readValue(jSon, Cidade[].class));
			String woeid = "455827";
			
			if(listaCidades != null && !listaCidades.isEmpty()){
				for(Cidade cidade : listaCidades){
					if(ipVigilante.getData().getCity_name().equals(cidade.getTitle())){
						woeid = cidade.getWoeid();
						break;
					}
				}
			}
			
			if(woeid != null){
				
				jSon = chamarServico("https://www.metaweather.com/api/location/"+woeid);
				Clima clima = mapper.readValue(jSon, Clima.class);
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date hoje = new Date();
				
				for(ClimaConsolidado climaConsolidado : clima.getConsolidated_weather()){
					if(climaConsolidado.getApplicable_date().equals(sdf.format(hoje))){
						
						pessoaDTO.setTempMin(climaConsolidado.getMin_temp());
						pessoaDTO.setTempMax(climaConsolidado.getMax_temp());
						break;
					}
				}
			}
			
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch(RuntimeException e){
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Ocorreu um erro no momento de salvar a pessoa.",null));
		}
		
		pessoaDTO = service.salvar(pessoaDTO);
		
		return ResponseEntity.status(HttpStatus.OK).body(new Response("Usuário cadastrado",pessoaDTO));
	}
	
	@GetMapping(path = "/buscar/{id}")
	public ResponseEntity<Response> buscarProduto(@PathVariable Long id) {
		
		try {
			
			PessoaDTO pessoaDTO = service.buscar(id);
			
			if(pessoaDTO == null || pessoaDTO.getCodigo() == null){
				return ResponseEntity.status(HttpStatus.OK).body(new Response("Pessoa não encontrada",pessoaDTO));
			}
			
			return ResponseEntity.status(HttpStatus.OK).body(new Response("Pessoa encontrada com sucesso",pessoaDTO));
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Erro ao buscar a pessoa",null));
		}
	}
	
	@DeleteMapping(path = "/delete/{id}")
	public ResponseEntity<Response> deletar(@PathVariable Long id) {
		
		if(id == null){
			return ResponseEntity.status(HttpStatus.OK).body(new Response("Informe o ID da pessoa",null));
		}else{
			
			PessoaDTO pessoaDTO = service.deletar(id);
			
			if(pessoaDTO == null){
				return ResponseEntity.status(HttpStatus.OK).body(new Response("Não existe uma pessoa com esse ID.",null));
			}
			
			return ResponseEntity.status(HttpStatus.OK).body(new Response("Pessoa deletada com sucesso",pessoaDTO));
		}
		
	}
	
	public String chamarServico(String urlServico){
		
		try {
    		
    		/* Start of Fix */
            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                public void checkServerTrusted(X509Certificate[] certs, String authType) { }

            } };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) { return true; }
            };
            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            /* End of the fix*/
    		
    		
	    	URL url = new URL(urlServico);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
	
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "+ conn.getResponseCode());
			}
	
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
	
			String output;
			
			String retorno = "";
			while ((output = br.readLine()) != null) {
				retorno+=output;
			}
			
			conn.disconnect();
			
			return retorno;
			
    	} catch (MalformedURLException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}	
		
		return null;
	}
	
}
