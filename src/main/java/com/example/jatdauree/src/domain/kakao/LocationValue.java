package com.example.jatdauree.src.domain.kakao;

import com.example.jatdauree.config.secret.KakaoSecret;
import com.example.jatdauree.src.domain.kakao.address.LocationInfoRes;
import com.example.jatdauree.src.domain.kakao.xypoint.LocationXYRes;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Getter
@Setter
public class LocationValue {
    private final int EARTH_RADIUS = 6731;

    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1); // latitude : 위도 y
        double dLon = Math.toRadians(lon2 - lon1); // longtitude : 경도 x

        double a = Math.sin(dLat/2)
                * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2)* Math.sin(dLon/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = EARTH_RADIUS* c * 1000;    // Distance with m
        return d;
    }

    public int getDuration(int distance){
        return distance / 100 * 96 / 60;
    }

    public double[] aroundDist(double seletX, double selectY, int distance){
        //m당 y 좌표 이동 값
        double mForLatitude =(1 /(EARTH_RADIUS* 1 *(Math.PI/ 180)))/ 1000;
        //m당 x 좌표 이동 값
        double mForLongitude =(1 /(EARTH_RADIUS* 1 *(Math.PI/ 180)* Math.cos(Math.toRadians(selectY))))/ 1000;
        int distanceM = distance;
        //현재 위치 기준 검색 거리 좌표
        double maxY = selectY +(distanceM* mForLatitude);
        double minY = selectY -(distanceM* mForLatitude);
        double maxX = seletX +(distanceM* mForLongitude);
        double minX = seletX -(distanceM* mForLongitude);

        /*System.out.println("maxY: "+ maxY);
        System.out.println("maxX: "+ maxX);
        System.out.println("minY: "+ minY);
        System.out.println("minX: "+ minX);*/

        return new double[]{minX, maxX, minY, maxY};
    }

    public ResponseEntity<LocationInfoRes>  kakaoLocalAPI(String query){
        String url = "https://dapi.kakao.com/v2/local/search/address.json?query=" + query;
        RestTemplate restTemplate = new RestTemplate();
        //HTTPHeader를 설정해줘야 하기때문에 생성함
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK "+ KakaoSecret.kakaoRestAPIKey);
        headers.set("content-type", "application/json;charset=UTF-8");
        HttpEntity<?> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                LocationInfoRes.class
        );
    }

    public ResponseEntity<LocationXYRes>  kakaoXYAPI(double x, double y){
        String url = "https://dapi.kakao.com/v2/local/geo/coord2address.json?"
                + "x=" + x
                + "&y=" + y
                + "&input_coord=WGS84";

        RestTemplate restTemplate = new RestTemplate();
        //HTTPHeader를 설정해줘야 하기때문에 생성함
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK "+ KakaoSecret.kakaoRestAPIKey);
        headers.set("content-type", "application/json;charset=UTF-8");
        HttpEntity<?> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                LocationXYRes.class
        );
    }
}
