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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

/**
 * The exception resolve is used to catch exceptions thrown by the controllers
 * and respond with a JSON error message.
 * 
 * @author jreed
 */
@Component("ExceptionResolver")
public class ApiExceptionHandler implements HandlerExceptionResolver {
    /** Logger. */
    private static final Logger log = Logger
            .getLogger(ApiExceptionHandler.class.getName());

    /**
     * Resolves an exception by converting it into HashMap and returning
     * it to Jackson to convert it to a JSON object.
     * 
     * @param request the request object
     * @param response the reponse object
     * @param handler the exception handler
     * @param e the exception being handled
     * @return the object to render as a MappingJacksonJsonView
     */
    public ModelAndView resolveException(HttpServletRequest request,
            HttpServletResponse response, Object handler, Exception e) {

        ModelAndView mav = new ModelAndView();
        mav.setView(new MappingJacksonJsonView());
        mav.addObject("errorType", e.getClass().getName());
        mav.addObject("errorMessage", e.getMessage());

        log.log(Level.WARNING, "Caught Exception: " + e, e);
        if (e instanceof SecurityException) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else if (e instanceof IllegalArgumentException) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return mav;
    }
}
