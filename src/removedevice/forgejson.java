package removedevice;
import java.io.IOException;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class forgejson {

	public static JSONObject forgeheaderreq(String org_alias,String token) {
	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");  
	LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);  
	
	
	JSONObject reqHeaderGetuser = new JSONObject();
	try {
		reqHeaderGetuser.put("locale", "en");
		reqHeaderGetuser.put("orgAlias", org_alias);
		reqHeaderGetuser.put("secretKey", token);
		reqHeaderGetuser.put("timestamp", dtf.format(timestamp));
		reqHeaderGetuser.put("version", "4.9");
	
		
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return reqHeaderGetuser;
	}
	
	
	
	
	
	
	public static JSONObject forgegetuserinfobodyreq(String subject){
		
		JSONObject reqBodyGetuser = new JSONObject();
		
		try {
			reqBodyGetuser.put("getSameDeviceUsers", false);
			reqBodyGetuser.put("userName", subject);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reqBodyGetuser;
	}
	
	
	public static JSONObject forgepayload(JSONObject reqHeaderGetuser,JSONObject reqBodyGetuser){
		JSONObject payload = new JSONObject();
		try {
			payload.put("reqHeader", reqHeaderGetuser);
			payload.put("reqBody", reqBodyGetuser);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return payload;
	}
	
	
	public static String forgejwt(String org_alias,String token,String secretKey,JSONObject payload){
		
		long ttlMillis=100000;
		 SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		 long nowMillis = System.currentTimeMillis();
		    Date now = new Date(nowMillis);
		    byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
		    Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
		    
		    //Let's set the JWT Claims
		    JwtBuilder builder = Jwts.builder()
		            //.setIssuedAt(now)
		            .signWith(signatureAlgorithm, signingKey);
		    builder.setHeaderParam("org_alias",org_alias);
		    builder.setHeaderParam("token",token);
		    builder.setPayload(payload.toString());
		
		return builder.compact();
	}
	
	public static String sendrequest(String tokenrequest,String url){

		String jwtrsult = "";
	    OkHttpClient client = new OkHttpClient().newBuilder()
	    		.build();
	    		MediaType mediaType = MediaType.parse("application/json");
	    		RequestBody body = RequestBody.create(mediaType,tokenrequest);
	    		Request request = new Request.Builder()
	    		  
	    		  .url(url)
	    		  .method("POST", body)
	    		  .build();
	    		try {
					Response response = client.newCall(request).execute();
					jwtrsult = response.body().string();
					//System.out.println(jwtrsult);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
		
	return jwtrsult;
	}
	
	
	
	public static List<String> getUserresetlist(String subject,String jwtrsult,List<String> userandroidmail){
		JSONArray userdevides = null;
		try {
			byte[] decodedBytes = Base64.getDecoder().decode(jwtrsult.split("\\.")[1]);
			String decodedString = new String(decodedBytes);
			JSONObject jsonpayload = new JSONObject(decodedString);
			userdevides = jsonpayload.getJSONObject("responseBody").getJSONObject("userDetails").getJSONArray("devicesDetails");
			System.out.print(subject + " " +userdevides.length()+ " " );
    //		writer.print(subject + " " +userdevides.length()+ " " );
    		for (int j = 0; j < userdevides.length(); j++) {
    		try {
				System.out.print(userdevides.getJSONObject(j).get("type")+" ");
	//			writer.print(userdevides.getJSONObject(j).get("type")+" ");
				if(userdevides.getJSONObject(j).get("type").equals("Android")||(userdevides.getJSONObject(j).get("type").equals("Email")&&userdevides.getJSONObject(j).get("email").toString().contains("@bricocenter.it"))) {userandroidmail.add(subject+","+userdevides.getJSONObject(j).get("deviceId").toString()+","+userdevides.getJSONObject(j).get("type"));}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				System.out.println(subject + " No_device " );
	   // 		writer.println(subject + " No_device " );
			}
    		
    		
    		}
    	//	System.out.println(i);
    	//	writer.println("");
		} catch (JSONException e) {
			System.out.println(subject + " No_device " );
    	//	writer.println(subject + " No_device " );
    		
		}
		
		return userandroidmail;
	}
	
	public static JSONObject forgedeletedevicebodyreq(String subject,String deviceId){
		
		JSONObject reqBodyGetuser = new JSONObject();
		
		try {
			reqBodyGetuser.put("userName", subject);
			reqBodyGetuser.put("deviceId", deviceId);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reqBodyGetuser;
	}
	
	
}
