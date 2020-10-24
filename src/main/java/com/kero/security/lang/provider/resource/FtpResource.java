package com.kero.security.lang.provider.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.StringJoiner;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kero.security.ksdl.provider.resource.KsdlTextResource;

public class FtpResource implements KsdlTextResource {

	private static Logger LOGGER = LoggerFactory.getLogger("Kero-Security-Ftp");

	private String server;
	private int port;
	
	private String username;
	private String pass;

	private String path;

	private Set<String> suffixes;
	
	public FtpResource(String server, int port, String username, String pass, String path, Set<String> suffixes) {

		this.server = server;
		this.port = port;
		
		this.username = username;
		this.pass = pass;
		
		this.path = path;
		
		this.suffixes = suffixes;
	}
	
	@Override
	public String getRawText() {
		
		FTPClient ftpClient = new FTPClient();
		
		try {
		
			LOGGER.info("Connecting to FTP server: "+server+":"+port);
			
			ftpClient.connect(server, port);
		
			int reply = ftpClient.getReplyCode();
			
			if(!FTPReply.isPositiveCompletion(reply)) {
	        
				ftpClient.disconnect();
	        	throw new IOException("Exception in connecting to FTP Server");
	        }
			
			LOGGER.info("Connected to FTP server.");	
			
			LOGGER.debug("Try login.");
			
			boolean login = ftpClient.login(this.username, this.pass);
		
			if(login) {
				
				LOGGER.debug("Login complete.");	
			}
			else {
			
				throw new RuntimeException("Login failed.");
			}
			
			FTPFile[] roots = ftpClient.listFiles(path);
			
			LOGGER.debug("Roots count: "+roots.length);
			
			StringJoiner schemesJoiner = new StringJoiner("\n");
			
			for(FTPFile root : roots) {
				
				parseSchemes(ftpClient, null, root, schemesJoiner);
			}
			
			return schemesJoiner.toString();
		}
		catch(Exception e) {
		
			throw new RuntimeException(e);
		}
	}
	
	private void parseSchemes(FTPClient client, FTPFile folder, FTPFile file, StringJoiner schemesJoiner) throws IOException {
		
		if(file.isFile()) {
			
			if(itSuitableFile(file.getName())) {
				
				String path = folder != null ? folder.getName()+"/"+file.getName() : file.getName();
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
				LOGGER.debug("Begin download: "+path);
				client.retrieveFile(path, baos);
				
				schemesJoiner.add(new String(baos.toByteArray()));
				
				baos.close();
			}
		}
		else if(file.isDirectory()) {
			
			FTPFile[] subs = client.listFiles(file.getName());
		
			for(FTPFile sub : subs) {
				
				parseSchemes(client, file, sub, schemesJoiner);
			}
		}
	}
	
	private boolean itSuitableFile(String name) {
		
		for(String suffix : suffixes) {
			
			if(name.endsWith(suffix)) {
				
				return true;
			}
		}
		
		return false;
	}
}
