package com.kero.security.core.agent.configurator;

import java.util.Set;

import com.kero.security.core.agent.KeroAccessAgent;
import com.kero.security.core.agent.configuration.KeroAccessAgentConfigurator;
import com.kero.security.core.scheme.configurator.KsdlAccessSchemeConfigurator;
import com.kero.security.lang.provider.KsdlProvider;
import com.kero.security.lang.provider.TextualProvider;
import com.kero.security.lang.provider.resource.FtpResource;
import com.kero.security.lang.provider.resource.KsdlTextResource;

public class AccessAgentFtpResourceConfigurator implements KeroAccessAgentConfigurator {

	private String server;
	private int port;
	
	private String username;
	private String pass;

	private String path;
	
	private Set<String> suffixes;

	private boolean resourceCacheEnabled;
	private boolean providerCacheEnabled;

	public AccessAgentFtpResourceConfigurator(String server, int port, String username, String pass, String path, boolean resourceCacheEnabled, boolean providerCacheEnabled, Set<String> suffixes) {
		
		this.server = server;
		this.port = port;
		
		this.username = username;
		this.pass = pass;
		
		this.path = path;
		
		this.suffixes = suffixes;
		
		this.resourceCacheEnabled = resourceCacheEnabled;
		this.providerCacheEnabled = providerCacheEnabled;
	}

	@Override
	public void configure(KeroAccessAgent agent) {
		
		KsdlTextResource resource = new FtpResource(server, port, username, pass, path, suffixes);
	
		if(this.resourceCacheEnabled) {
			
			resource = KsdlTextResource.addCacheWrap(resource);
		}
		
		KsdlProvider provider = new TextualProvider(resource);
		
		if(this.providerCacheEnabled) {
			
			provider = KsdlProvider.addCacheWrap(provider);
		}
		
		agent.addConfigurator(new KsdlAccessSchemeConfigurator(provider));
	}
}