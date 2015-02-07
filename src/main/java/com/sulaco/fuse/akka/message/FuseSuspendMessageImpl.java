package com.sulaco.fuse.akka.message;

public class FuseSuspendMessageImpl extends FuseInternalMessageImpl implements FuseSuspendMessage {

    public FuseSuspendMessageImpl(FuseRequestMessage request) {
        super(request);
    }

    public FuseSuspendMessageImpl(FuseInternalMessage message) {
        super(message);
    }
}
