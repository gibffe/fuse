package com.sulaco.fuse.akka.message;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;

import akka.actor.ActorRef;

public class FuseInternalMessageImpl implements FuseInternalMessage {

	protected FuseMessageContext ctx;
	
	protected Deque<ActorRef> chain;

    protected long timestamp;

	public FuseInternalMessageImpl(FuseRequestMessage request) {
		this();
		ctx.setRequest(request);
	}

    public FuseInternalMessageImpl(FuseInternalMessage message) {
        timestamp();
        this.ctx = message.getContext();
        this.chain = message.getChain();
    }
	
	public FuseInternalMessageImpl() {
        timestamp();
        this.ctx   = new FuseMessageContextImpl();
        this.chain = new LinkedList<>();
	}
	
	@Override
	public Optional<ActorRef> popOrigin() {
		return Optional.ofNullable(chain.pop());
	}

	@Override
	public void pushOrigin(ActorRef actorRef) {
		chain.push(actorRef);
	}

	@Override
	public FuseMessageContext getContext() {
		return ctx;
	}

    @Override
    public Deque<ActorRef> getChain() {
        return chain;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public FuseInternalMessage timestamp() {
        timestamp = System.currentTimeMillis();
        return this;
    }

    @Override
    public long getRequestId() {
        return ctx.getRequest().get().getId();
    }

}
