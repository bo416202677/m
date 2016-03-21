package cn.m.web.handler;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.omg.CORBA.UserException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import cn.m.util.enums.ErrorEnum;

import com.alibaba.fastjson.JSON;

@Component
public class ExceptionHandler implements HandlerExceptionResolver {

	private static final Logger LOGGER = Logger
			.getLogger(ExceptionHandler.class);

	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object arg2, Exception ex) {
		try {
			LOGGER.info("reqUrl:" + request.getServletPath() + ", error mess: ", ex);
			if (ex instanceof BindException
					|| ex instanceof TypeMismatchException
					|| ex instanceof MissingServletRequestParameterException
					|| ex instanceof NullPointerException) {
				returnErrMessToClient(response, ErrorEnum.ERR_PARAMETER_ILLEGAL);
			}else if(ex instanceof Exception || ex instanceof UserException){
				returnErrMessToClient(response, ErrorEnum.ERR_SYS_FAIL);
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return new ModelAndView();
	}

	private void returnErrMessToClient(HttpServletResponse response,
			ErrorEnum error) throws IOException {
		// 返回错误信息不为空
		response.setHeader("content-type", "application/json");
		response.setCharacterEncoding("UTF-8");
		Writer writer = response.getWriter();
		writer.write(JSON.toJSONString(ErrorEnum.getErrorMap(error)));
		writer.close();
	}
}
