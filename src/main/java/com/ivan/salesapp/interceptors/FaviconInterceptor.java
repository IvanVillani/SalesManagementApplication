package com.ivan.salesapp.interceptors;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class FaviconInterceptor extends HandlerInterceptorAdapter {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String link = "https://res.cloudinary.com/ivanski-cloud/image/upload/v1607774493/588a66c2d06f6719692a2d1e_zmx2hv.png";

        if (modelAndView != null) {
            modelAndView.addObject("icon", link);
        }
    }


}