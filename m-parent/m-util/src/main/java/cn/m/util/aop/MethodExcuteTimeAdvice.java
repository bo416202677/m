package cn.m.util.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;

import cn.m.util.utils.StringUtil;

public class MethodExcuteTimeAdvice implements MethodInterceptor {

	private static final Logger LOGGER = Logger
			.getLogger(MethodExcuteTimeAdvice.class);

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		// 用 commons-lang 提供的 StopWatch 计时，Spring 也提供了一个 StopWatch
		StopWatch clock = new StopWatch();
		clock.start(); // 计时开始
		Object result = invocation.proceed();
		clock.stop(); // 计时结束
		// 方法参数类型，转换成简单类型
		Class<?>[] params = invocation.getMethod().getParameterTypes();
		Object[] arguments = invocation.getArguments();
		String[] simpleParams = new String[params.length];
		for (int i = 0; i < params.length; i++) {
			simpleParams[i] = params[i].getSimpleName() + " " + ObjectUtils.toString(arguments[i]);
		}
		LOGGER.info(new StringBuilder().append("[").append(invocation.getThis().getClass().getName())
				.append(".").append(invocation.getMethod().getName()).append("(")
				.append(StringUtil.join(simpleParams, ",")).append(")]").append(", excute time ")
				.append(clock.getTime()).append(", result ").append(result));
		return result;
	}

}
