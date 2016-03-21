package cn.m.test.rmi.impl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import cn.m.test.rmi.IHello;

public class HelloImpl extends UnicastRemoteObject implements IHello{

	public HelloImpl() throws RemoteException {
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String helloWorld() throws RemoteException {
		return "hello world!";
	}

	@Override
	public String sayHelloToSomeBody(String personName) throws RemoteException {
		System.err.println(personName+"调用啦");
		return "你好," + personName + "!";
	}

}
