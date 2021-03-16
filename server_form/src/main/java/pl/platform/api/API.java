package pl.platform.api;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class API {


    public static String getProxyCamera( String proxyIp,String proxyPort,String category){

        String url = "http://"+proxyIp+":"+proxyPort+"/"+category+"/getAllDevice";
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.getForObject(url,String.class);
    }



    public static String sentRequestToProxyWithOneParameter( String url,String specialCode){

        Map<String, String> map = new HashMap<>();
        map.put("specialCode",specialCode);
        String realUrl = url+"?specialCode={specialCode}";
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(realUrl,String.class,map);
    }



    public static String sentRequestToProxyWithTwoParameter( String url,String specialCode,String command){

        Map<String, String> map = new HashMap<>();
        map.put("specialCode",specialCode);
        map.put("command",command);
        String realUrl = url+"?specialCode={specialCode}&command={command}";
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(realUrl,String.class,map);
    }

    public static String sentRequestToProxyWithThreeParameter( String url,String specialCode,String command,String index){

        Map<String, String> map = new HashMap<>();
        map.put("specialCode",specialCode);
        map.put("command",command);
        map.put("index",index);
        String realUrl = url+"?specialCode={specialCode}&command={command}&index={index}";
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(realUrl,String.class,map);
    }



    public static String sentRequestToMoveOnvifOSD( String url,String specialCode,String command,String xPos,String yPos){
        Map<String, String> map = new HashMap<>();
        map.put("specialCode",specialCode);
        map.put("command",command);
        map.put("xPos",xPos);
        map.put("yPos",yPos);
        String realUrl = url+"?specialCode={specialCode}&command={command}&xPos={xPos}&yPos={yPos}";
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(realUrl,String.class,map);
    }


}
