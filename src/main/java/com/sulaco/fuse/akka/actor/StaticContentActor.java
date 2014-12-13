package com.sulaco.fuse.akka.actor;


import com.sulaco.fuse.akka.message.FuseRequestMessage;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StaticContentActor extends FuseEndpointActor {

    @Value("#{systemProperties.contentDir}")
    String contentDir;

    public StaticContentActor(ApplicationContext ctx) {
        super(ctx);
    }

    @Override
    protected void onRequest(FuseRequestMessage request) {

        try {
            Path path = Paths.get(contentDir + request.getRequest().getUri())
                             .toRealPath();

            proto.stream(request, path);
        }
        catch (IOException ex) {
            proto.error(request, HttpResponseStatus.NOT_FOUND);
        }
    }
}
