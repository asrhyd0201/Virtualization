package net.floodlightcontroller.dhcp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.devicemanager.Device;
import net.floodlightcontroller.devicemanager.DeviceAttachmentPoint;
import net.floodlightcontroller.devicemanager.IDeviceManagerService;
import net.floodlightcontroller.linkdiscovery.SwitchPortTuple;
import net.floodlightcontroller.packet.DHCP;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.UDP;

import org.openflow.protocol.OFPacketOut;
import org.openflow.protocol.OFPort;
import org.openflow.protocol.OFType;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionOutput;

public class PacketOutPusher extends Thread {
	
	IDeviceManagerService deviceManager;
	protected DHCP dhcp;
	protected IFloodlightProviderService floodlightProvider;
	
	public PacketOutPusher(DHCP dhcp, IDeviceManagerService deviceManager, IFloodlightProviderService floodlightProvider){
		this.dhcp=dhcp;
		this.deviceManager = deviceManager; 
		this.floodlightProvider = floodlightProvider;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		UDP udpPacket=new UDP();
		System.out.println("DHCP Packet Length : "+dhcp.serialize().length);
		udpPacket.setPayload(dhcp);
		udpPacket.setDestinationPort((short) 68);
		udpPacket.setSourcePort((short) 67);
		
		IPv4 ipv4Packet = new IPv4();
		ipv4Packet.setPayload(udpPacket);
		ipv4Packet.setDestinationAddress(dhcp.getClientIPAddress());
		ipv4Packet.setProtocol(IPv4.PROTOCOL_UDP);
		
		Ethernet ethPacket =new Ethernet();
		ethPacket.setPayload(ipv4Packet);
		ethPacket.setDestinationMACAddress(dhcp.getClientHardwareAddress());
		ethPacket.setEtherType(Ethernet.TYPE_IPv4);
		ethPacket.setSourceMACAddress(new byte[6]);
		/*
		//udpPacket.deserialize(dhcp.serialize(), 0, dhcp.serialize().length);
		udpPacket.setDestinationPort((short) 68);
		IPv4 ipv4Packet=new IPv4();
		ipv4Packet.deserialize(udpPacket.serialize(), 0, udpPacket.serialize().length);
		ipv4Packet.setProtocol(IPv4.PROTOCOL_UDP);
		ipv4Packet.setDestinationAddress("ff:ff:ff:ff:ff:ff");
		Ethernet ethPacket =new Ethernet();
		ethPacket.deserialize(ipv4Packet.serialize(), 0, ipv4Packet.serialize().length);
		ethPacket.setSourceMACAddress(new byte[6]);
		ethPacket.setDestinationMACAddress(dhcp.getClientHardwareAddress());
		ethPacket.setEtherType(Ethernet.TYPE_IPv4);
		*/
		
		Device dstDevice = deviceManager.getDeviceByDataLayerAddress(dhcp.getClientHardwareAddress());
		System.out.println("Getting the switch details for sending PACKET_OUT message\n");
		
        for (DeviceAttachmentPoint existingAttachmentPoint:
            dstDevice.getAttachmentPoints()) {
        	
        	 SwitchPortTuple swTuple = existingAttachmentPoint.getSwitchPort();
        	 IOFSwitch newSwitch = swTuple.getSw();
        	 short port = swTuple.getPort();
        	 OFPacketOut po=createOFPacketOut(ethPacket.serialize(),port);
        	 pushPacketOut(newSwitch, po);
        	
        }	
        
	}
	
	public OFPacketOut createOFPacketOut(byte[] data,short port)
	{
		OFPacketOut po = (OFPacketOut) floodlightProvider.getOFMessageFactory().getMessage(OFType.PACKET_OUT);
		
		short packetOutLength = (short)OFPacketOut.MINIMUM_LENGTH;
        po.setBufferId(OFPacketOut.BUFFER_ID_NONE);
        po.setInPort(OFPort.OFPP_NONE); 
        po.setActionsLength((short) OFActionOutput.MINIMUM_LENGTH);
        packetOutLength += OFActionOutput.MINIMUM_LENGTH;
        
		List<OFAction> actions = new ArrayList<OFAction>(1);
        actions.add(new OFActionOutput(port,(short)0));
        po.setActions(actions);
        
		po.setPacketData(data);
		packetOutLength += data.length; 
        po.setLength(packetOutLength);
		return po;		
	}
	
	public void pushPacketOut(IOFSwitch sw,OFPacketOut po)
	{
		/**
		 * Pushes the PacketOut messages to switch
		 */
		try 
		{
			sw.write(po, null);
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}