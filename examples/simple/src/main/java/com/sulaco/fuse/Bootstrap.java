package com.sulaco.fuse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sulaco.fuse.FuseServer;

public class Bootstrap {
	
	private AbstractApplicationContext ctx;
	private FuseServer server;
		
	protected void initContext() {
		
		this.initContext("/context.xml");
	}
	
	protected void initContext(String path) {
		try { 
			this.ctx    =  new ClassPathXmlApplicationContext(path);
			this.server = (FuseServer) this.ctx.getBean(FuseServer.class);
		}
		catch (Exception ex) { 
			log.error("[boot] Context initialisation failed !\n\n", ex);
			throw new IllegalStateException(ex);
		}
	}
	
	public void run() throws Exception {
		
		log.info("[boot] Server starting");
		
		this.server
		    .startServer()
		    .get();
		
		log.info("[boot] Server done");
	}
	
	public static void main(String[] args) throws Exception {
		
		log.info("[boot] Server booting...");
		
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.initContext();
		bootstrap.run();
	}
	
	private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);
}
