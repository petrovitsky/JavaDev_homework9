package org.example;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(value = "/")
public class TimezoneValidateFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String param = req.getParameter("timezone");
        String timezoneOffset = null;
        String timezone = null;

        if (param == null) {
            chain.doFilter(req,res);
        }

        if (param.length() > 4) {
            timezoneOffset = param
                    .substring(3)
                    .replaceAll(" ", "+");
            timezone = param.substring(0, 3);
        }
        if (validTimeZone(timezoneOffset, timezone)) {
            chain.doFilter(req, res);
            return;
        }
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        res.getWriter().write("Invalid timezone");
        res.getWriter().close();
    }

    private static boolean validTimeZone(String timezoneOffset, String timeZone) {
        int offSet;
        try {
            offSet = Integer.parseInt(timezoneOffset);
        } catch (NumberFormatException e) {
            return false;
        }
        return (offSet > -13 && offSet < 15) && ("UTC".equals(timeZone));
    }
}
