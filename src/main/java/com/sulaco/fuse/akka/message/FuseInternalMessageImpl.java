package com.sulaco.fuse.akka.message;

import java.util.Deque;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;

import akka.actor.ActorRef;

public class FuseInternalMessageImpl implements FuseInternalMessage, FuseOriginChain {

	protected FuseMessageContext ctx;
	
	protected Deque<ActorRef> chain;
	
	public FuseInternalMessageImpl(FuseRequestMessage request) {
		this();
		ctx.setRequest(request);
	}
	
	public FuseInternalMessageImpl() {
		super();
		this.ctx   = new FuseMessageContextImpl();
		this.chain = new ConcurrentLinkedDeque<>();
	}
	
	@Override
	public Optional<ActorRef> popOrigin() {
		return Optional.ofNullable(chain.pollLast());
	}

	@Override
	public void pushOrigin(ActorRef actorRef) {
		chain.push(actorRef);
	}

	@Override
	public FuseMessageContext getContext() {
		return ctx;
	}

}
