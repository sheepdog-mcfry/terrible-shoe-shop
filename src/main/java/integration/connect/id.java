package integration.connect;

/*

import com.telenor.oauth.client.OAuthClient;
import org.apache.commons.lang.StringUtils;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.velocity.VelocityTemplateEngine;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static spark.Spark.*;
import static spark.Spark.get;

/**
 * Created by Waseem on 12/05/16.
 */

/*
public class id {
    private static Logger log = Logger.getLogger(id.class.getName());

    public static String LOA_1 = "1";
    public static String LOA_2 = "2";
    private static String[] VALID_LOA_VALUES = {LOA_1, LOA_2};
    private static String SESSION_COOKIE_KEY = "sessionId";
    private static String BAD_STATIC_STATE = "StaticStateIsBad!";
    private static byte[] TOKEN_ENCRYPTION_KEY = new BigInteger("285DCB9EFB68C262DD74730A6EEE8F046C5FD5D10FEF1B34F3F2AF1B2D0DD8E7", 16).toByteArray();
    private static OAuthClient oauthClient;
    private static ClientConfig clientConfig;
    private static boolean isLocalhost;
    private static String serviceIdWithoutEnvironment;

    public static void main(String[] args) throws Exception {

        Config config = new Config(args);
        if(config.parsingFailed()) { System.exit(1); }

        String serviceId = config.getServiceId();
        serviceIdWithoutEnvironment = serviceId.split("_")[0];
        String templatePath = "public/"+serviceIdWithoutEnvironment+"/"+serviceIdWithoutEnvironment+".vm";
        clientConfig = ClientConfig.valueOf(serviceId.toUpperCase());
        isLocalhost = !config.isNotLocalhost();
        oauthClient = createOAuthClient();

        staticFileLocation("/public");
        port(config.isNotLocalhost() ? config.getPort() : 19999);
        before(new ImportantSecurityFilter());
        get("/", (request, response) -> render(createModel(request, getLoginUserId(request), false), templatePath));
        get("/oauth2callback",  (request, response) -> handleOauthCallback(request, response, templatePath));
        get("/postlogout",      (request, response) -> handlePostLogout(response));

    }

    private static AuthorizationCodeGrantFlow createAuthCodeGrantFlow(String serviceId, String levelOfAssurance, int maxAge) {
        String enforceSpecificClaimValueForEmailClaim = null;
        ClaimRequest claimRequest;
        switch (serviceId) {
            case "strusic":
                claimRequest = new ClaimRequest.Builder().addUserInfoClaim(Claim.EMAIL, new ClaimRequestProperties(true, enforceSpecificClaimValueForEmailClaim)).build();
                break;
            case "hipstagram":
            case "allthenews":
            default:
                claimRequest = null;
                break;
        }
        return oauthClient
                .startAuthorizationCodeGrantFlow(clientConfig.getRedirectUri(isLocalhost), new HashSet<>(Arrays.asList("openid")), BAD_STATIC_STATE)
                .setMaxAge(maxAge) //time before sso expires
                .setAcrValues(Arrays.asList(levelOfAssurance))
                .setClaims(claimRequest)
                .build();
    }

    private static String getLoa(Request request, String serviceId) {
        String loa = request.queryParams("loa");
        if (StringUtils.isNotEmpty(loa) && Arrays.asList(VALID_LOA_VALUES).contains(loa)) {
            return loa;
        }
        switch (serviceId) {
            case "strusic":
            case "allthenews":
                return LOA_2;
            case "hipstagram":
            default:
                return LOA_1;
        }
    }

    private static OAuthClient createOAuthClient() throws IOException {
        Client httpClient = ClientBuilder.newClient();
        httpClient.property(ClientProperties.CONNECT_TIMEOUT, clientConfig.oauthClientConnectTimeout);
        httpClient.property(ClientProperties.READ_TIMEOUT, clientConfig.oauthReadConnectTimeout);
        return new OAuthClient(new Configuration.Builder()
                .setBasicCredentials(new BasicCredentials(clientConfig.getClientId(), clientConfig.getClientSecret()))
                .setIssuer(clientConfig.getIssuer())
                .setTokenEncryptionKey(TOKEN_ENCRYPTION_KEY)
                .addTrustedAudience(clientConfig.getClientId())
                .setHttpClient(httpClient)
                .autoConfigure());
    }

    public static String handleOauthCallback(Request request, Response response, String templatePath) {
        boolean userAbortedSignIn;
        String code = request.queryParams("code");
        String error = request.queryParams("error");
        String errorDescription = request.queryParams("error_description");
        if (error != null && "access_denied".equals(error)) {
            log.warning("Something went wrong during authentication! Message: " + errorDescription);
            return render(createModel(request, getLoginUserId(request), userAbortedSignIn = true), templatePath);
        }
        String sessionId;
        String levelOfAssurance = getLoa(request, serviceIdWithoutEnvironment);
        AuthorizationCodeGrantFlow authorizationCodeGrantFlow = createAuthCodeGrantFlow(serviceIdWithoutEnvironment, levelOfAssurance, 0);
        try {
            sessionId = oauthClient.encryptTokenSet(authorizationCodeGrantFlow.createTokenRequest(code, BAD_STATIC_STATE).execute());
        } catch (IOException e) {
            log.log(Level.WARNING, "Something went wrong during token fetching!", e);
            return render(createModel(request, getLoginUserId(request), userAbortedSignIn = false), templatePath);
        }
        response.cookie(SESSION_COOKIE_KEY, sessionId);
        response.redirect("/");
        return null;
    }

    public static String handlePostLogout(Response response) {
        response.removeCookie(SESSION_COOKIE_KEY);
        response.redirect("/");
        return null;
    }

    private static String getLoginUserId(Request request) {
        String sessionCookie = request.cookie(SESSION_COOKIE_KEY);
        if (sessionCookie == null) {
            return null;
        }
        TokenSet tokenSet;
        try {
            tokenSet = oauthClient.decryptTokenSet(sessionCookie);
        } catch (IOException e) {
            log.log(Level.WARNING, "Got corrupt sessionId: " + sessionCookie, e);
            return null;
        }
        try {
            ReadOnlyJWTClaimsSet claims = oauthClient.getUserInfo(new BearerCredentials(tokenSet.getAccessToken()));
            return claims.getSubject();
        } catch (IOException e) {
            log.log(Level.WARNING, "SessionID contains invalid access token.", e);
        }
        return null;
    }

    public static Map createModel(Request request, String loginUserId, boolean userAbortedSignIn) {
        Map<String, Object> model = new HashMap<>();
        model.put("userAbortedSignIn", userAbortedSignIn);
        if (loginUserId != null) {
            model.put("loggedIn", true);
        } else {
            model.put("loggedIn", false);
        }
        String levelOfAssurance = getLoa(request, serviceIdWithoutEnvironment);
        AuthorizationCodeGrantFlow authorizationCodeGrantFlow = createAuthCodeGrantFlow(serviceIdWithoutEnvironment, levelOfAssurance, 0);
        model.put("authorizeUri", authorizationCodeGrantFlow.getAuthorizeUri().toASCIIString());
        model.put("logoutUri", UriBuilder.fromUri(clientConfig.getIssuer())
                .path("logout")
                .queryParam("client_id", clientConfig.getClientId())
                .queryParam("post_logout_redirect_uri", clientConfig.getPostLogoutRedirectUri(isLocalhost))
                .build());
        return model;
    }

    public static String render(Map model, String templatePath) {
        return new VelocityTemplateEngine().render(new ModelAndView(model, templatePath));
    }
}

*/