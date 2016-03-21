package cn.m.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import cn.m.util.utils.CommonUtil;
import cn.m.util.utils.DateUtil;

import com.alibaba.fastjson.JSON;

@WebFilter(urlPatterns="*.do")
public class CommonFilter implements Filter {
	
	private static final Logger LOGGER = Logger.getLogger(CommonFilter.class);

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		long start = System.currentTimeMillis();
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String curURL = httpRequest.getServletPath(); 
		long mthTracTime = CommonUtil.getMethodTrackingTimeStamp(request.getParameter("mthTracTime"), start);
		try {
			LOGGER.info(mthTracTime + ", " + curURL + " req params:" + JSON.toJSONString(request.getParameterMap()));
			chain.doFilter(request, response);
			LOGGER.info(mthTracTime + ", " + curURL + " excute time is " + DateUtil.getDiffTimeToCurForSpecifiedTime(start));
		} catch (Exception e) {
			LOGGER.error(mthTracTime + ", " + curURL + " req met exception:", e);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void destroy() {
	}
}
