package net.floodlightcontroller.dhcp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.devicemanager.IDeviceManagerService;
import net.floodlightcontroller.packet.DHCP;
import net.floodlightcontroller.threadpool.IThreadPoolService;



public class DHCPResponseListener implements Runnable {

	protected IThreadPoolService threadPool;									
	protected IDeviceManagerService deviceManager;
	protected IFloodlightProviderService floodlightProvider;

	protected String service;
	protected int portNumber;
	private static final int DHCP_RESPONSE_TIMER_INTERVAL = 1;
	
	DatagramSocket Socket;
	DatagramPacket receivePacket;
	byte[] receiveData= new byte[1500];
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		responseListener();
		
		ScheduledExecutorService ses =threadPool.getScheduledExecutor();
		ses.schedule(this,DHCP_RESPONSE_TIMER_INTERVAL, TimeUnit.SECONDS );
	}
	
	public DHCPResponseListener(String name, int port, IThreadPoolService threadPool, 
			IDeviceManagerService deviceManager, IFloodlightProviderService floodlightProvider){
		this.service = name;
		this.threadPool = threadPool;
		this.portNumber = port;
		this.deviceManager = deviceManager;
		this.floodlightProvider = floodlightProvider;
	}
	
	public void responseListener()
	{
		try 
		{
			Socket = new DatagramSocket(portNumber);
			InetAddress IPAddress = InetAddress.getByName("10.1.1.109");
			
			System.out.println("DHCPResponseHandler : Waiting for the DHCP response packets on port 6668");
			while(true)
			{
				//System.out.println("\nTesting\n");
				
				receivePacket = new DatagramPacket(receiveData, receiveData.length, IPAddress,portNumber);
				Socket.receive(receivePacket);
				System.out.println("\nTesting\n");
				System.out.println("\nTesting\n");
				System.out.println("\nTesting\n");
				System.out.println("\nTesting\n");
				
				System.out.println("\nTesting\n");
				System.out.println("\nTesting\n");
				System.out.println("\nTesting\n");
				//DHCPPacket dhcp = DHCPPacket.getPacket(receivePacket);
			    System.out.println("Recieved the DHCP reply message\n");
				
				DHCP dhcpPacket=new DHCP();
				dhcpPacket.deserialize(receiveData, 0, receiveData.length);
				
				PacketOutPusher po = new PacketOutPusher(dhcpPacket, deviceManager, floodlightProvider);
				po.start();
				
			}
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}