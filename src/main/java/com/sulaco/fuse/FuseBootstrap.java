package com.sulaco.fuse;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class FuseBootstrap {

    protected AbstractApplicationContext ctx;
    protected FuseServer server;

    protected FuseBootstrap initContext() {
        return initContext("/context.xml");
    }

    protected FuseBootstrap initContext(String path) {
        try {
            this.ctx    =  new ClassPathXmlApplicationContext(path);
            this.server = (FuseServer) this.ctx.getBean(FuseServer.class);
        }
        catch (Exception ex) {
            log.error("[boot] Context initialisation failed !\n\n", ex);
            throw new IllegalStateException(ex);
        }

        return this;
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

        new FuseBootstrap()
            .initContext()
            .run();
    }

    static final Logger log = LoggerFactory.getLogger(FuseBootstrap.class);
}
