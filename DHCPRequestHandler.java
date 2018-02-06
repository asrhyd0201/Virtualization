package net.floodlightcontroller.dhcp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import net.floodlightcontroller.packet.DHCP;

public class DHCPRequestHandler implements Runnable {
	
	byte[] sendData;
	InetAddress IPAddress;
	DatagramSocket clientSocket;
	DatagramPacket sendPacket;
	int port=67;
	DHCP dhcp;
	
	DHCPRequestHandler(DHCP dhcp)
	{
		this.dhcp=dhcp;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		/**
		 * Sends request to the dhcp relay agent
		 */
		try 
		{
			sendData=dhcp.serialize();	
			
			System.out.println("DHCP message option code" + dhcp.getOption(DHCP.DHCPOptionCode.OptionCode_MessageType).toString());
			
			clientSocket = new DatagramSocket();
			IPAddress = InetAddress.getByName("10.1.1.109");
			sendPacket = new DatagramPacket(sendData,sendData.length, IPAddress,port);
			clientSocket.send(sendPacket);
			System.out.println("Sent DHCP request of length " +  sendData.length +"\n");
			//System.out.println("Sent DHCP request length " +  dhcp.serialize().length +"\n");
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			System.out.println("DHCP Error: SocketException");
			e.printStackTrace();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("DHCP Error: UnKnownHostException");
			e.printStackTrace();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("DHCP Error: IOException");
			e.printStackTrace();
		
		}
	}
}