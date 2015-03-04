package com.sulaco.fuse.config;

import akka.actor.ActorRef;
import com.sulaco.fuse.akka.actor.FuseBaseActor;
import com.sulaco.fuse.akka.actor.FuseEndpointActor;
import com.sulaco.fuse.config.actor.ActorFactory;
import com.sulaco.fuse.config.annotation.FuseActor;
import com.sulaco.fuse.config.annotation.FuseEndpoint;
import com.sulaco.fuse.config.route.RoutesConfig;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;

@Component
public class AnnotationScannerImpl implements AnnotationScanner {

    @Autowired ConfigSource config;

    @Autowired ActorFactory factory;

    @Autowired RoutesConfig routes;

    Reflections reflections;

    String packages;
    boolean verbose = false;

    public void init() {

        packages = config.getConfig().getString("fuse.scan.packages");
        verbose  = config.getConfig().getBoolean("fuse.scan.verbose");

        if (!StringUtils.isEmpty(packages)) {

            // prepare reflections config builder
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.addScanners(new TypeAnnotationsScanner());

            // add packages to scan
            Arrays.stream(packages.split(","))
                  .forEach(
                      name -> builder.addUrls(ClasspathHelper.forPackage(name))
                  );

            reflections = new Reflections(builder);
        }
    }

    @Override
    public void scan() {

        if (reflections != null) {

            reflections.getTypesAnnotatedWith(FuseActor.class)
                       .forEach(this::processActor);

            reflections.getTypesAnnotatedWith(FuseEndpoint.class)
                       .forEach(this::processEndpoint);
        }
    }

    void processActor(Class<?> clazz) {

        if (FuseBaseActor.class.isAssignableFrom(clazz)) {

            if (verbose) {
                log.info("processing {}", clazz.getCanonicalName());
            }

            // create new general purpose actor
            //
            FuseActor annotation = clazz.getAnnotation(FuseActor.class);

            String id   = StringUtils.isEmpty(annotation.id()) ? clazz.getName() : annotation.id();
            int    spin = annotation.spin();

            factory.getLocalActor(id, clazz.getName(), spin);
        }
        else {
            if (verbose) {
                log.warn("{} not a FuseBase actor !", clazz.getCanonicalName());
            }
        }
    }

    void processEndpoint(Class<?> clazz) {
        if (FuseEndpointActor.class.isAssignableFrom(clazz)) {

            if (verbose) {
                log.info("processing {}", clazz.getCanonicalName());
            }

            // create new endpoint actor
            //
            FuseEndpoint annotation = clazz.getAnnotation(FuseEndpoint.class);

            int spin = annotation.spin();

            Optional<ActorRef> ref = factory.getLocalActor(clazz.getName(), clazz.getName(), spin);
            ref.ifPresent(
                actor -> {
                    routes.addEndpoint(
                            actor,
                            annotation.method(),
                            annotation.path()
                    );
                }
            );
        }
        else {
            log.warn("{} not a FuseEndpointActor !", clazz.getCanonicalName());
        }
    }

    static final Logger log = LoggerFactory.getLogger(AnnotationScannerImpl.class);
}
