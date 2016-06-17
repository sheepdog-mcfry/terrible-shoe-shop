package terribleshoeshop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import secret.ClientConfig;
/**
 * Created by Waseem on 15/06/16.
 */
public class OAuth {

    public static String returnCredentials() {
        String userPass = "";
        ClientConfig crazywaseem = new ClientConfig();
        userPass = crazywaseem.clientId+":"+crazywaseem.clientSecret;
        return userPass;
    }

    public static boolean authenticate(String token) throws IOException {


        boolean authenticated = false;

        String urlBuilder = "https://connect.staging.telenordigital.com/oauth/tokeninfo?access_token="+token;

        URL url = new URL(urlBuilder);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches (false);

        String resObj = processResponse(conn.getInputStream());

        JSONObject jsonresponse = new JSONObject(resObj);

        if (jsonresponse.has("error")) {
            return false;
        };

        String clientid = jsonresponse.getString("clientid");

        if (clientid.equals("telenordigital-waseemshoes-web")) {
            authenticated = true;
        };

        return authenticated;
    }

    public static String getToken(String code) throws IOException {
        final String grant_type = "authorization_code";
        final String redirect_uri = "http://localhost:8081/connect/oauth2callback&client_id=telenordigital-waseemshoes-web";
        URL url = new URL("https://connect.staging.telenordigital.com/oauth/token");
        String token = "";

        String userPassword = returnCredentials();
        String userpass = javax.xml.bind.DatatypeConverter.printBase64Binary(userPassword.getBytes("UTF-8"));

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches (false);

        String param="grant_type=authorization_code"
                +"&code="+code
                +"&redirect_uri="+redirect_uri;


        conn.setRequestProperty("Authorization", "Basic "+userpass);

        PrintWriter out = new PrintWriter(conn.getOutputStream());
        out.write(param);
        out.close();

        String resObj = processResponse(conn.getInputStream());

        JSONObject jsonToken = new JSONObject(resObj);

        if (jsonToken.has("error")) {
            return "";
        };
        token = jsonToken.getString("access_token");
        return token;
    }

    public static String processResponse(InputStream in) throws IOException {
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();

        String inputStr;
        while ((inputStr = streamReader.readLine()) !=null)
            responseStrBuilder.append(inputStr);
        return responseStrBuilder.toString();
    }

    public static void logout(String token) throws IOException {

        URL url = new URL("https://connect.staging.telenordigital.com/oauth/revoke");

        String userPassword = returnCredentials();
        String userpass = javax.xml.bind.DatatypeConverter.printBase64Binary(userPassword.getBytes("UTF-8"));

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches (false);

        String param = "token="+token;

        conn.setRequestProperty("Authorization", "Basic "+userpass);

        PrintWriter out = new PrintWriter(conn.getOutputStream());
        out.write(param);
        out.close();

        new InputStreamReader(conn.getInputStream());

    }
}
