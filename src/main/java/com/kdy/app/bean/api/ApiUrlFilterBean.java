package com.kdy.app.bean.api;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ApiUrlFilterBean implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		String[] uri = req.getRequestURI().split("/");
		
		if(uri[1].equals("p")) {
			if(uri.length >= 3) {
				req.getRequestDispatcher(req.getContextPath()+"/api/v2.0/live-player/"+uri[2]).forward(req, res);
			} else {
				req.getRequestDispatcher(req.getContextPath()+"/api/v2.0/live-player/null").forward(req, res);
			}
		} else if(uri[1].equals("v")) {
			if(uri.length >= 3) {
				req.getRequestDispatcher(req.getContextPath()+"/api/v2.0/vod-player/"+uri[2]).forward(req, res);
			} else {
				req.getRequestDispatcher(req.getContextPath()+"/api/v2.0/vod-player/null").forward(req, res);
			}
			
		} else {
			chain.doFilter(req, res);
		}
		
	}

}
