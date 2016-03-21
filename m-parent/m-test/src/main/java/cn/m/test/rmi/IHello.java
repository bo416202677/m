package cn.m.test.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IHello extends Remote{

	String helloWorld() throws RemoteException;
	
	String sayHelloToSomeBody(String personName) throws RemoteException;
}
