package net.floodlightcontroller.dhcp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.devicemanager.IDeviceManagerService;
import net.floodlightcontroller.threadpool.IThreadPoolService;

public class DHCPResponseHandler implements IFloodlightModule {

	protected IFloodlightProviderService floodlightProvider;
    protected IDeviceManagerService deviceManager;
    protected DHCPResponseListener responsePusher;
    protected IThreadPoolService threadPool;
    
    private static final String SERVICE	="DHCP Reponse Listener"; 
    private static final int DHCP_RESPONSE_RECV_PORT = 6668;
    
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		// TODO Auto-generated method stub
		Collection<Class<? extends IFloodlightService>> l = 
			new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		l.add(IDeviceManagerService.class);
		l.add(IThreadPoolService.class);
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
        deviceManager = context.getServiceImpl(IDeviceManagerService.class);
        threadPool = context.getServiceImpl(IThreadPoolService.class);
	}



	@Override
	public void startUp(FloodlightModuleContext context) {
		// TODO Auto-generated method stub
		threadInvoker();
	}
	
	public void threadInvoker(){
		if(responsePusher != null)
			return;
		
		
		responsePusher = new DHCPResponseListener(SERVICE, DHCP_RESPONSE_RECV_PORT, threadPool,deviceManager, floodlightProvider);
        threadPool.getScheduledExecutor().schedule(responsePusher, 5, TimeUnit.SECONDS);
        

	}
}