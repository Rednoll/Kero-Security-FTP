package com.kero.security.core.agent.configurator;

import java.util.Set;

import com.kero.security.ksdl.agent.KsdlAgent;
import com.kero.security.ksdl.agent.configuration.KsdlAgentConfigurator;
import com.kero.security.ksdl.provider.resource.KsdlTextResource;
import com.kero.security.lang.provider.resource.FtpResource;

public class KsdlAgentFtpResourceConfigurator implements KsdlAgentConfigurator {

	private String server;
	private int port;
	
	private String username;
	private String pass;

	private String path;
	
	private Set<String> suffixes;

	public KsdlAgentFtpResourceConfigurator(String server, int port, String username, String pass, String path, Set<String> suffixes) {
		
		this.server = server;
		this.port = port;
		
		this.username = username;
		this.pass = pass;
		
		this.path = path;
		
		this.suffixes = suffixes;
	}

	@Override
	public void configure(KsdlAgent agent) {
		
		KsdlTextResource resource = new FtpResource(server, port, username, pass, path, suffixes);
		
		agent.addTextResource(resource);
	}
}