/*
 * Copyright (c) 2013 Jordan Reed 
 * 
 * This file is part of Weather.Next.
 * 
 * Weather.Next is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Weather.Next is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Weather.Next.  If not, see <http://www.gnu.org/licenses/>.
 */
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
    /** Logger. */
    private static final Logger log = Logger.getLogger(CorsFilter.class
            .getName());

    /**
     * Applies the filter by adding the CORS header.
     * 
     * @param request the http request
     * @param response the http response
     * @param filterChain the filter chain to apply to
     * @throws ServletException exception working with servlet objects
     * @throws IOException exception writing to the reponse
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String origin = request.getHeader("Origin");
        if (origin != null) {
            response.addHeader("X-Received-Origin", origin.replaceAll("\n", "")
                    .replaceAll("\r", ""));
        }
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS");

        filterChain.doFilter(request, response);
    }

}
