package cn.m.http.test.script;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptMainTest {

	public static void main(String[] args) throws FileNotFoundException, ScriptException, NoSuchMethodException {
		// 获取javascript执行引擎
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
		
		// 建立上下文变量
		Bindings binding = engine.createBindings();
		binding.put("factor", 1);
		
		engine.setBindings(binding, ScriptContext.ENGINE_SCOPE);
		// 绑定上下文,作用域是当前引擎范围
		Scanner input = new Scanner(System.in);
		while (input.hasNextInt()){
			int first = input.nextInt();
			int sec = input.nextInt();
			System.err.println("输入参数:" + first + "," + sec);
			
			// 执行js代码
			engine.eval(new FileReader(ScriptMainTest.class.getClassLoader().getResource("scripts/model.js").getFile()));
			
			// 是否可调用方法
			if(engine instanceof Invocable){
				Invocable in = (Invocable) engine;
				Double result = (Double) in.invokeFunction("formula", first, sec);
				System.err.println("运算结果:" + result.intValue());
			}
		}
		input.close();
	}

}
