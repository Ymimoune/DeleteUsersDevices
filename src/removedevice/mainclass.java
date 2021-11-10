package removedevice;

import removedevice.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import okhttp3.*;


public class mainclass {

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException{

		//set variable
		String secretKey = "bicIGKc+MXF+XS4ICB3E3YYNfdMUqpn2HRgJypwMyc0=";
		String token = "e872cc66ac0d7497bb27c61efa984a80";
		String org_alias = "c1879608-ef26-4825-aba1-b3fd905ba61f";
		String CSV_path = "exportBCIT.csv";
		String getuserURL = "https://idpxnyl3m.pingidentity.eu/pingid/rest/4/getuserdetails/do";
		String deleteDeviceURL = "https://idpxnyl3m.pingidentity.eu/pingid/rest/4/unpairdevice/do";
		String row = "";
		List<String> subjects = new ArrayList<String>();
		List<String> userdevice = new ArrayList<String>();
		BufferedReader csvReader;
		
			PrintWriter writer = new PrintWriter("mon-fichier.txt", "UTF-8");


		try {
			csvReader = new BufferedReader(new FileReader(CSV_path));
			while ((row = csvReader.readLine()) != null) {
			    String[] data = row.split(",");
			    subjects.add(data[4].replace("\"", ""));
			    // do something with the data
			   // System.out.println(data[4].replace("\"", ""));
			}
			csvReader.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		//create request header for get user request
		for (int i = 0; i < subjects.size(); i++) {
		String subject = subjects.get(i);
		
		JSONObject reqHeaderGetuser = forgejson.forgeheaderreq(org_alias,token);

		JSONObject reqBodyGetuser = forgejson.forgegetuserinfobodyreq(subject);
				
		JSONObject payload = forgejson.forgepayload(reqHeaderGetuser, reqBodyGetuser);
		
		String tokenrequest = forgejson.forgejwt(org_alias, token, secretKey, payload);
		
		String jwtrsult = forgejson.sendrequest(tokenrequest, getuserURL);
		  
		Claims jwtpayload = Jwts.parser()
		    	            .setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
		    	            .parseClaimsJws(jwtrsult).getBody();
		
		
		userdevice = forgejson.getUserresetlist(subject, jwtrsult, userdevice);   		
		    		

		}
		
		writer.close();
		for (int j = 0; j < userdevice.size(); j++) {
			System.out.println(userdevice.get(j));
			JSONObject reqHeaderdeletedevice = forgejson.forgeheaderreq(org_alias,token);
			JSONObject reqBodyGetuser = forgejson.forgedeletedevicebodyreq(userdevice.get(j).split(",")[0],userdevice.get(j).split(",")[1]);
			JSONObject payload = forgejson.forgepayload(reqHeaderdeletedevice, reqBodyGetuser);

			String tokenrequest = forgejson.forgejwt(org_alias, token, secretKey, payload);
			String jwtrsult = forgejson.sendrequest(tokenrequest, deleteDeviceURL);
			Claims jwtpayload = Jwts.parser()
    	            .setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
    	            .parseClaimsJws(jwtrsult).getBody();
			System.out.println(jwtpayload.get("responseBody"));
		}
	}

}
