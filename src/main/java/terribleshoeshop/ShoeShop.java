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
        staticFileLocation("/resources/");

        get("/index", (req, res) -> {
            return new ModelAndView(new HashMap(), "templates/index.vm");
        }, new VelocityTemplateEngine());

        post("/buy", (req, res) -> {
            return new ModelAndView(new HashMap(), "templates/buy.vm");
        }, new VelocityTemplateEngine());

        post("/login", (req, res) -> {
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
