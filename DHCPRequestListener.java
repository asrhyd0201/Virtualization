package net.floodlightcontroller.dhcp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.packet.DHCP;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.UDP;

import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFType;

public class DHCPRequestListener implements IOFMessageListener, IFloodlightModule {
	protected IFloodlightProviderService floodlightProvider;
	public IOFSwitch sw;
	public FloodlightContext cntx=new FloodlightContext();
	
	@Override
	public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		// TODO Auto-generated method stub
		Ethernet eth =
            IFloodlightProviderService.bcStore.get(cntx,
                                        IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		
		/**
		 * get the packets and filter the dhcp packets
		 */
		if (eth.getPayload() instanceof IPv4) 
		{
            IPv4 ipv4 = (IPv4) eth.getPayload();
            if (ipv4.getPayload() instanceof UDP) 
            {
                UDP udp = (UDP)ipv4.getPayload();
                if (udp.getPayload() instanceof DHCP) 
                {
                    DHCP dhcp = (DHCP)udp.getPayload();
                    //System.out.println("Sending dhcp packet");
                    //if(dhcp.getOpCode() == DHCP.OPCODE_REQUEST){
                    	DHCPRequestHandler connection=new DHCPRequestHandler(dhcp);
                    	Thread t = new Thread(connection);
                    	t.start();
                   // }
                }
            }
		}
		return Command.CONTINUE;
	}

	

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "DHCPListener";
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		// TODO Auto-generated method stub
		Collection<Class<? extends IFloodlightService>> l =
	        new ArrayList<Class<? extends IFloodlightService>>();
	    l.add(IFloodlightProviderService.class);
	    return l;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		// TODO Auto-generated method stub
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
	}

	@Override
	public void startUp(FloodlightModuleContext context) {
		// TODO Auto-generated method stub
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
	}

}