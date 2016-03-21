package cn.m.test.rmi.server;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import cn.m.test.rmi.IHello;
import cn.m.test.rmi.impl.HelloImpl;

public class HelloServer {

	public static void main(String[] args) {
		try {
			IHello rhello = new HelloImpl();
			LocateRegistry.createRegistry(8888);
			
			Naming.bind("rmi://localhost:8888/RHello",rhello); 
			
			System.err.println(">>>>INFO:远程IHello对象绑定成功！");
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			e.printStackTrace();
		}
	}
}
