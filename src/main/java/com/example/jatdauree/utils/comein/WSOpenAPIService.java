package com.example.jatdauree.utils.comein;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class WSOpenAPIService implements Constants {

    @Value("${whois.api.key}")
    private String apiKey;

    @Value("${whois.api.uri}")
    private String apiUri;

    @Autowired
    private CloseableHttpClient closeableHttpClient;

    @Autowired
    private RequestConfig requestConfig;


    private final Logger logger = LoggerFactory.getLogger(getClass());

    @SuppressWarnings("unchecked")
    public Map<String,String> getClientInfoByIPAddress(String ip) {

        ObjectMapper objectMapper = null;

        try {

            StringBuilder urlBuilder = new StringBuilder(apiUri);
            urlBuilder.append("?"+ URLEncoder.encode("query", "UTF-8") + "="+ip);
            urlBuilder.append("&"+ URLEncoder.encode("serviceKey", "UTF-8") + "="+apiKey);
            urlBuilder.append("&"+ URLEncoder.encode("answer", "UTF-8") + "=JSON");

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json;charset=UTF-8");

            BufferedReader rd;
            // 서비스코드가 정상이면 200~300사이의 숫자가 나옵니다.
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();

            if(conn.getResponseCode() == HttpStatus.OK.value()) {
                String json = sb.toString();
                logger.info("WHO IS API Response json : "+json);
                objectMapper = new ObjectMapper();

                Map<String,Map<String,String>> map = objectMapper.readValue(json, Map.class);

                return map.get("whois");

            }else{
                String json = sb.toString();
                logger.info("WHO IS API Response failed: ");
                System.out.println(json);
                return null;
            }
        } catch (ClientProtocolException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return null;
        }  catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}