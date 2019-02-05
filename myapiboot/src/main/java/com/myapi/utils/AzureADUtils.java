package com.myapi.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.StringTokenizer;
import java.util.Base64.Decoder;
import java.util.HashMap;
import java.util.Map;
import java.io.*;
import java.math.BigInteger;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.myapi.azure.KeyBean;
import com.myapi.azure.OpenIdConfigurationBean;
import com.myapi.azure.OpenIdKeysBean;
import com.myapi.exception.TokenException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
@PropertySource("classpath:application.properties")
public class AzureADUtils {
	
	@Value("${azure.openid.config.url}")
	private String AZURE_OPENID_CONFIG_URL;
	
	@Value("${azure.applicationid}")
	private String APPLICATION_ID;
	
	private static Logger log = Logger.getLogger(AzureADUtils.class);
	
	private Map<String,Object> getTokenComponents(String idToken) throws TokenException{
		final Decoder decoder = Base64.getDecoder();
		final StringTokenizer tokenizer = new StringTokenizer(idToken,".");
		int i = 0;
		Map<String, Object> tokenHeader = new HashMap<String, Object>();
		Map<String, Object> tokenBody = new HashMap<String, Object>();
		String signatureJws = "";
		final Map<String, Object> tokenMapParts = new HashMap<String, Object>();
		
		//(1) DECODE THE 3 PARTS OF THE JWT TOKEN
		try {
			while(tokenizer.hasMoreElements()) {
				if(i == 0) {
					tokenHeader = string2JSONMap(new String(decoder.decode(tokenizer.nextToken())));
				}else if(i == 1){
					tokenBody = string2JSONMap(new String(decoder.decode(tokenizer.nextToken())));
				}else {
					signatureJws = new String(tokenizer.nextToken());
					log.info(signatureJws);
				}
				i++;
			}
		}catch(IOException e) {
			throw new TokenException(500,e.getMessage());
		}
		
		//(1.1) THE 3 PARTS OF THE TOKEN SHOULD BE IN PLACE
		if(tokenHeader == null || tokenBody == null || signatureJws == null || tokenHeader.isEmpty() || tokenBody.isEmpty() || signatureJws.isEmpty() ) {
			throw new TokenException(500,"Invalid Token");
		}
		
		tokenMapParts.put("header", tokenHeader);
		tokenMapParts.put("body", tokenBody);
		tokenMapParts.put("signature", signatureJws);
		
		return tokenMapParts;
	}
	
	@SuppressWarnings("unchecked")
	public String getUsernameFromToken(String idToken) throws TokenException{
		String username = "";
		final Map<String,Object> map = getTokenComponents(idToken);
		if(map != null && map.containsKey("body")) {
			Map<String, Object> tokenBody = (Map<String, Object>)map.get("body");
			if(tokenBody != null && tokenBody.containsKey("emails")) {
				java.util.ArrayList<String> usernames = (java.util.ArrayList<String>)tokenBody.get("emails");
				if(usernames != null && !usernames.isEmpty()) {
					username = usernames.get(0);
				}
			}
		}
		return username;
	}
	
	@SuppressWarnings("unchecked")
	public boolean isNewUsernameFromToken(String idToken) throws TokenException{
		boolean isNewUser = false;
		final Map<String,Object> map = getTokenComponents(idToken);
		if(map != null && map.containsKey("body")) {
			Map<String, Object> tokenBody = (Map<String, Object>)map.get("body");
			if(tokenBody != null && tokenBody.containsKey("newUser")) {
				Boolean isnewObj = (Boolean)tokenBody.get("newUser");
				if(isnewObj != null && isnewObj.booleanValue() ) {
					isNewUser = true;
				}
			}
		}
		return isNewUser;
	}
	
	@SuppressWarnings("unchecked")
	public boolean validateToken(String idToken) {
		boolean isValidToken = false;
		try {
			final Map<String,Object> mapTokenComponents = getTokenComponents(idToken);
			final Map<String, Object> tokenHeader = (Map<String, Object>)mapTokenComponents.get("header");
			final String signatureJws = (String)mapTokenComponents.get("signature");
			
			//(2) GET OPENID CONFIGURATIONS AND SELECT THE MACHING KEY BEAN
			final String keysUrl = callOpenidConfiguration().getJwks_uri();
			log.info(keysUrl);
			KeyBean keyBeanForAccess = null;
			for(KeyBean keyBean : discoveryKeys(keysUrl).getKeys()) {
				log.info(keyBean.getKid());
				if(keyBean.getKid().equals( (String)tokenHeader.get("kid") )) {
					keyBeanForAccess = keyBean;
					break;
				}
			}
			
			//(3) VALIDATE THE JWT CLAIMS
			PublicKey pubKeyNew;
			Claims claims;
			try {
				byte[] modulusBytes = Base64.getUrlDecoder().decode(keyBeanForAccess.getN());
				byte[] exponentBytes = Base64.getUrlDecoder().decode(keyBeanForAccess.getE());
				BigInteger modulusInt = new BigInteger(1, modulusBytes);
				BigInteger exponentInt = new BigInteger(1, exponentBytes);
				KeySpec publicSpec = null;
				
				KeyFactory keyFactory = KeyFactory.getInstance(keyBeanForAccess.getKty());
				switch(keyBeanForAccess.getKty()) {
					case "RSA": publicSpec = new RSAPublicKeySpec(modulusInt, exponentInt);
								break;
				}
			    pubKeyNew = keyFactory.generatePublic(publicSpec);
				claims = Jwts.parser()
				          .setSigningKey(pubKeyNew)
				            .parseClaimsJws(idToken).getBody();
				log.info("Expiration Date:: " + claims.getExpiration().toString());
				log.info("Issued Date:: " + claims.getIssuedAt().toString());
				log.info("Issuer:: " + claims.getIssuer());
				log.info("Audience:: " + claims.getAudience());
			}catch(Exception e) {
				throw new TokenException(500,"Invalid claims: " + e.getMessage());
			}
			
			if(claims == null || !APPLICATION_ID.equals(claims.getAudience())) {
				throw new TokenException(500,"Invalid audience claim");
			}
			
			//(4) VERIFY SIGNATURE
			try {
				byte[] signature = Base64.getUrlDecoder().decode(signatureJws);
				Signature sig = Signature.getInstance("SHA256withRSA");
				sig.initVerify(pubKeyNew);
				sig.update(idToken.getBytes());
				log.info(sig.verify(signature));
			}catch(Exception e) {
				throw new TokenException(500,"Invalid signature: " + e.getMessage());
			}
			
			isValidToken = true;
		
		}catch(TokenException e) {
			log.warn("Invalid token!",e);
		}
		
		return isValidToken;
	}

	/**
	 * @author joseortiz
	 * @param url
	 * @return
	 */
	public OpenIdConfigurationBean callOpenidConfiguration(String url) {
		AZURE_OPENID_CONFIG_URL = url;
		return callOpenidConfiguration();
	}
	
	/**
	 * @author joseortiz
	 * @return
	 */
	public OpenIdConfigurationBean callOpenidConfiguration() {
	      OpenIdConfigurationBean openIdConfigurationBean = new OpenIdConfigurationBean();
	      try {
		     final URL url = new URL(AZURE_OPENID_CONFIG_URL);
		     final HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
		     
		     if(con!=null){
		 		try {
		 			
		 		   BufferedReader br =
		 			new BufferedReader(
		 				new InputStreamReader(con.getInputStream()));

		 		   String input;
		 		   StringBuilder builder = new StringBuilder();

		 		   while ((input = br.readLine()) != null){
		 			  builder.append(input);
		 		   }
		 		   br.close();
		 		   
		 		  ObjectMapper mapper = new ObjectMapper();
		 		  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		 		 openIdConfigurationBean = mapper.readValue(builder.toString(), OpenIdConfigurationBean.class);

		 		} catch (IOException e) {
		 		   e.printStackTrace();
		 		}

		 	}

	      } catch (MalformedURLException e) {
		     e.printStackTrace();
	      } catch (IOException e) {
		     e.printStackTrace();
	      }
	      return openIdConfigurationBean;
	}
	
	/**
	 * @author joseortiz
	 * @param keysURL
	 * @return
	 */
	public OpenIdKeysBean discoveryKeys(String keysURL) {
	      OpenIdKeysBean openIdKeysBean = new OpenIdKeysBean();
	      try {
		     final URL url = new URL(keysURL);
		     final HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
		     if(con!=null){
		 		try {
		 		   BufferedReader br =
		 			new BufferedReader(
		 				new InputStreamReader(con.getInputStream()));

		 		   String input;
		 		   StringBuilder builder = new StringBuilder();

		 		   while ((input = br.readLine()) != null){
		 			  builder.append(input);
		 		   }
		 		   br.close();
		 		   
		 		  ObjectMapper mapper = new ObjectMapper();
		 		  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		 		 openIdKeysBean = mapper.readValue(builder.toString(), OpenIdKeysBean.class);

		 		} catch (IOException e) {
		 		   e.printStackTrace();
		 		}

		 	}

	      } catch (MalformedURLException e) {
		     e.printStackTrace();
	      } catch (IOException e) {
		     e.printStackTrace();
	      }
	      return openIdKeysBean;
	}
	
	public Map<String, Object> string2JSONMap(String json) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		// convert JSON string to Map
		return mapper.readValue(json, new TypeReference<Map<String, Object>>(){});

	}
}
