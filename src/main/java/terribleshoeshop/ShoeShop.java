/**
 * Created by Waseem on 04/05/16.
 */
package terribleshoeshop;

import static spark.Spark.*;
import static spark.debug.DebugScreen.*;

import java.util.HashMap;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

public class ShoeShop{
    public static void main(String[] args){
        staticFileLocation("/public");

        port(8081);

        get("/index", (req, res) -> {
            return new ModelAndView(new HashMap(), "templates/index.vm");
        }, new VelocityTemplateEngine());

        before("/buy", (req, res)->{

            boolean authenticated = false;

            if (req.session().attribute("token") != null) {
                String notVerySecretToken = req.session().attribute("token");
                authenticated = OAuth.authenticate(notVerySecretToken);
            }
            if (!authenticated) {
                res.redirect("https://connect.staging.telenordigital.com/oauth/authorize?response_type=code&client_id=telenordigital-waseemshoes-web&redirect_uri=http://localhost:8081/connect/oauth2callback&scope=openid+profile+email+phone+id.user.read+id.user.write+id.user.email.read+id.user.phone.read+id.user.right.read+id.user.right.use+id.user.sub.read+id.user.account.read+payment.transactions.read+payment.transactions.write+payment.agreements.read+payment.agreements.write+telenordigital.waseemshoes&state= RANDOMVALUE&ui_locales=en&login_hint=4798545206&login_hint=user@example.com&acr_values=2");
            }

        });

        get("/buy", (req, res) -> {
            return new ModelAndView(new HashMap(), "templates/buy.vm");
        }, new VelocityTemplateEngine());

        get("/connect/oauth2callback", (req, res) -> {
            String notVerySecretCode = req.queryParams("code");
            String notVerySecretToken = OAuth.getToken(notVerySecretCode);
            req.session(true);
            req.session().attribute("token", notVerySecretToken);
            res.redirect("/buy");
            return "";
        });

        get("/logout", (req, res) -> {
            OAuth.logout(req.session().attribute("token"));
            req.session().removeAttribute("token");
            res.redirect("/index");
            return "";
        });


        get("/login", (req, res) -> {
            return new ModelAndView(new HashMap(), "templates/login.vm");
        }, new VelocityTemplateEngine());

        get("/success",(req, res) -> {
            return new ModelAndView(new HashMap(), "templates/success.vm");
        }, new VelocityTemplateEngine());

        get("/fail",(req, res) -> {
            throw new Exception("Successful Error");
        });

        enableDebugScreen();
    }
}
