package com.sulaco.fuse.metrics;

import org.springframework.stereotype.Component;

import com.codahale.metrics.MetricRegistry;

@Component
public class MetricsRegistry {

	final MetricRegistry metrics = new MetricRegistry();
	
	public MetricRegistry getRegistry() {
		return metrics;
	}
	
}
