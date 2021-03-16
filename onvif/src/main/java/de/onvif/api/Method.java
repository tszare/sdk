package de.onvif.api;

import com.sun.net.httpserver.HttpExchange;
        import org.onvif.ver10.schema.PTZSpeed;
        import org.onvif.ver10.schema.Vector1D;
        import org.onvif.ver10.schema.Vector2D;

        import java.io.IOException;
        import java.io.OutputStream;
        import java.io.UnsupportedEncodingException;
        import java.net.URLDecoder;
        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.LinkedHashMap;
        import java.util.Map;

public class Method {
    public static Map<String, String> formData2Dic(String formData) {
        Map<String, String> result = new LinkedHashMap<>();
        if (formData == null || formData.trim().length() == 0) {
            return result;
        }
        final String[] items = formData.split("&");
        Arrays.stream(items).forEach(item -> {
            final String[] keyAndVal = item.split("=");
            if (keyAndVal.length == 2) {
                try {
                    final String key = URLDecoder.decode(keyAndVal[0], "utf8");
                    final String val = URLDecoder.decode(keyAndVal[1], "utf8");
                    result.put(key, val);
                } catch (UnsupportedEncodingException e) {
                }
            }
        });
        return result;
    }





    public static PTZSpeed MoveSpeed(Float x, Float y, Float z){



        PTZSpeed speed = new PTZSpeed();
        Vector2D vector2D = new Vector2D();
        Vector1D vector1D = new Vector1D();
        vector1D.setX(z);
        vector2D.setX(x);
        vector2D.setY(y);
        speed.setPanTilt(vector2D);
        speed.setZoom(vector1D);
        return speed;

    }

    public static void Output (String respond, HttpExchange exchange) throws IOException {

        exchange.getResponseHeaders().add("Content-Type:", "text/html;charset=utf-8");
        exchange.sendResponseHeaders(200, 0);
        OutputStream os = exchange.getResponseBody();
        os.write(respond.getBytes());
        os.close();


    }


}




