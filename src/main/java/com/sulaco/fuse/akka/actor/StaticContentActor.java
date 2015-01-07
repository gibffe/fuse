package com.sulaco.fuse.akka.actor;


import com.sulaco.fuse.akka.message.FuseRequestMessage;
import com.sulaco.fuse.config.ConfigSource;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StaticContentActor extends FuseEndpointActor {

    String contentDir = null;

    @Autowired ConfigSource configSource;

    public StaticContentActor(ApplicationContext ctx) {
        super(ctx);
    }

    @Override
    protected void onRequest(FuseRequestMessage request) {

        initConfig();

        try {
            // TODO: implement path security check - we should only be albe to see content under contentDir
            //
            Path path = Paths.get(contentDir + request.getRequest().getUri())
                             .toRealPath();

            proto.stream(request, path);
        }
        catch (IOException ex) {
            proto.error(request, HttpResponseStatus.NOT_FOUND);
        }
    }

    void initConfig() {
        if (contentDir == null) {
            contentDir = configSource.getConfig().getString("fuse.content.dir");
        }
    }
}
