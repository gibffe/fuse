package com.sulaco.fuse;

import org.springframework.stereotype.Component;

@Component
public class FuseVersion {

	String version = "Fuse v0.0.1-SNAPSHOT";
	
	public FuseVersion() {
		super();
	}
	
	public String toString() {
		return version;
	}
}
