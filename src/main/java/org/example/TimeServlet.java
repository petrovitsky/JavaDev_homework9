package org.example;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Map;

@WebServlet(value = "/")
public class TimeServlet extends HttpServlet {
    private final String COOKIE_NAME = "lastTimezone";
    private TemplateEngine engine;

    @Override
    public void init(ServletConfig config) throws ServletException {
        engine = new TemplateEngine();
        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix("C:/Users/epetr/eclipse-workspace/JavaDev/Homework9/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        ZonedDateTime utcDateTime = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC);
        String sufix = "UTC";
        String lastTimezone = getLastTimezone(request);

        if(lastTimezone != null) {
            sufix = lastTimezone;
            utcDateTime = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.of(lastTimezone.substring(3)));
        }


        if (request.getParameter("timezone") != null && !request.getParameter("timezone").isEmpty()) {
            String timezoneParam = request.getParameter("timezone").substring(3).replaceAll(" ", "+");
            utcDateTime = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.of(timezoneParam));
            sufix = request.getParameter("timezone").replaceAll(" ", "+");
            response.addCookie(new Cookie("lastTimezone", sufix));
        }

        DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss").appendLiteral(" " + sufix).toFormatter();
        String formattedDateTime = utcDateTime.format(formatter);

        response.setContentType("text/html; charset=utf-8");
        final Context context = new Context(
                response.getLocale()
                , Map.of("time", formattedDateTime));

        engine.process("time", context, response.getWriter());
        response.getWriter().close();
    }

    private String getLastTimezone(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
