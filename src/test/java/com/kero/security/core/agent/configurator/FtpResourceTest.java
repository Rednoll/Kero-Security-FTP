package com.kero.security.core.agent.configurator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

import com.kero.security.lang.provider.resource.FtpResource;

public class FtpResourceTest {
	
	@Test
	public void test() {
		
		FakeFtpServer server = new FakeFtpServer();
			server.addUserAccount(new UserAccount("test", "test", "/home/test"));
			server.setServerControlPort(7777);
			
		FileSystem fileSystem = new UnixFakeFileSystem();
			fileSystem.add(new DirectoryEntry("/home/test"));
			fileSystem.add(new FileEntry("/home/test/scheme.k-s", "test"));
			fileSystem.add(new DirectoryEntry("/home/test/sub"));
			fileSystem.add(new FileEntry("/home/test/sub/scheme.k-s", "kek"));
			
		server.setFileSystem(fileSystem);
		
		server.start();
		
		Set<String> suffixes = new HashSet<>();
			suffixes.add(".k-s");
			suffixes.add(".ks");
			
		FtpResource res = new FtpResource("localhost", server.getServerControlPort(), "test", "test", "/home/test", suffixes);
	
		String rawText = res.getRawText();
	
		assertEquals("test\nkek", rawText);
	}
}
