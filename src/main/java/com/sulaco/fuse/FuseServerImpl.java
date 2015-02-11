package com.sulaco.fuse;

import com.sulaco.fuse.config.AnnotationScanner;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.RoundRobinRouter;

import com.sulaco.fuse.akka.actor.RouteFinderActor;
import com.sulaco.fuse.akka.syslog.SystemLogActor;
import com.sulaco.fuse.config.ConfigSource;
import com.sulaco.fuse.config.actor.ActorFactory;
import com.sulaco.fuse.netty.FuseChannelInitializer;
import com.typesafe.config.Config;

@Component
public class FuseServerImpl implements FuseServer, InitializingBean, ApplicationContextAware {

    protected ExecutorService exe = Executors.newSingleThreadExecutor();
    
    protected ActorSystem actorSystem;
    protected ApplicationContext appContext;
    
    @Autowired protected ActorFactory actorFactory;
    @Autowired protected FuseChannelInitializer channelInitializer;
    @Autowired protected ConfigSource configSource;
    @Autowired protected AnnotationScanner annotationScanner;
    
    public FuseServerImpl() {       
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
                
        log.info("[fuse] Creating actor system");
        actorSystem = ActorSystem.create("fuse", configSource.getConfig());
        actorFactory.setActorSystem(actorSystem);

        log.info("[fuse] Creating system logger");
        actorSystem.actorOf(Props.create(SystemLogActor.class));

        log.info("[fuse] Initializing routing");
        configSource.parseLocalConfig();

        log.info("[fuse] Running annotation scanner");
        annotationScanner.scan();

        // create router actor and pass the reference to channelInitializer
        channelInitializer
            .setRouter(
                actorSystem.actorOf(
                    Props.empty()
                         .withRouter(
                             RoundRobinRouter.create(getRouteFinders())
                         )                          
                )
            );

        // create suspended animator
        actorFactory.getLocalActor(
            "animator",
            "com.sulaco.fuse.akka.actor.SuspendedAnimationActor",
            "animator",
            configSource.getConfig().getInt("fuse.animator.spin")
        );
    }
    
    @Override
    public FutureTask<Integer> startServer() {

        return (FutureTask<Integer>) exe.submit(
                () -> {
                    startNetty(actorSystem);
                    return -1;
                }
        );
    }

    protected void startNetty(ActorSystem system) {
        
        log.info("[fuse] Starting netty...");
        
        Config config = configSource.getConfig();

        EventLoopGroup bossGroup   = new NioEventLoopGroup(config.getInt("netty.boss.eventloop.threads"));
        EventLoopGroup workerGroup = new NioEventLoopGroup(config.getInt("netty.work.eventloop.threads"));
        
        try {
            ServerBootstrap boot = new ServerBootstrap();
            boot.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(channelInitializer)
                .option(ChannelOption.TCP_NODELAY, true);

            Channel channel = boot.bind(config.getInt("fuse.port"))
                                  .sync()
                                  .channel();

            log.info("[fuse] netty:{} GET /fuse/status for more info", config.getInt("fuse.port"));
            
            channel.closeFuture()
                   .sync();
        } 
        catch (Exception ex) {
            log.error("Error starting netty !", ex);
            throw new RuntimeException(ex);
        } 
        finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }   
    }
    
    protected Iterable<ActorRef> getRouteFinders() {

        int spin = configSource.getConfig().getInt("fuse.route.finders");

        return actorFactory.getRoutees("route_finder", RouteFinderActor.class, spin);
    }

    public void setSystem(ActorSystem system) {
        this.actorSystem = system;
    }

    @Override
    public void setApplicationContext(ApplicationContext appContext) throws BeansException {
        this.appContext = appContext;
    }

    private static final Logger log = LoggerFactory.getLogger(FuseServerImpl.class);
}
