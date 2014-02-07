package net.chaosserver.weathernext.weather.web;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Cors filter is designed to properly handle CORS request and reply in kind.
 * 
 * @author jreed
 */
public class CorsFilter extends OncePerRequestFilter {
    private static final Logger log = Logger.getLogger(CorsFilter.class
            .getName());

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String origin = request.getHeader("Origin");
        if (origin != null) {
            response.addHeader("X-Received-Origin", origin);
        }
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS");

        filterChain.doFilter(request, response);
    }

}
