package com.sulaco.fuse.akka.actor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.ApplicationContext;

import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;
import com.sulaco.fuse.akka.message.FuseRequestMessage;

public class ServerStatusActor extends FuseEndpointActor {

	public void serverStatus(FuseRequestMessage message) {
        actorStatus(message);
	}

	public void actorStatus(FuseRequestMessage message) {
		
		Map<String, Object> stats = new HashMap<>();
		
		Optional<String> metricName = message.getParam(ACTOR_CLASS);
		
		if (metricName.isPresent()) {
			Timer timer = metrics.getRegistry().timer(metricName.get());
			if (timer != null) {
				fillStats(stats, timer);
			}
		}
		
		proto.respond(message, stats);
	}
	
	protected void fillStats(Map<String, Object> stats, Timer timer) {

		stats.put("count", timer.getCount());
		
		// request rates
		double[] rate = new double[3];
				 rate[0] = timer.getOneMinuteRate();
				 rate[1] = timer.getFiveMinuteRate();
				 rate[2] = timer.getFifteenMinuteRate();
				 
		stats.put("rate", rate);
		
		// request latencies
		
		Snapshot snapshot = timer.getSnapshot();
		Map<String, Object> latency = new HashMap<>();
		
		latency.put("p99", snapshot.get99thPercentile());
		latency.put("p95", snapshot.get95thPercentile());
		latency.put("max", snapshot.getMax());
		latency.put("min", snapshot.getMin());
		latency.put("avg", snapshot.getMean());
		
		stats.put("latency", latency);
	}
	
	private static final String ACTOR_CLASS = "actorClass";
}
