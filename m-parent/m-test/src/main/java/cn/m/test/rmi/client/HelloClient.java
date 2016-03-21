package cn.m.test.rmi.client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import cn.m.test.rmi.IHello;

public class HelloClient {

	public static void main(String[] args) {
		try {
			IHello rhello = (IHello) Naming.lookup("rmi://localhost:8888/RHello");
			System.out.println(rhello.helloWorld()); 
            System.out.println(rhello.sayHelloToSomeBody("熔岩")); 
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}
}
