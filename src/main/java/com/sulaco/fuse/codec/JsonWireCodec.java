package com.sulaco.fuse.codec;

import com.google.gson.Gson;

public class JsonWireCodec implements WireCodec {

	private Gson gson;
	
	public JsonWireCodec() {
		super();
		this.gson = new Gson();
	}
	
	@Override
	public <T> T getObject(String data, Class<T> clazz) {
		return gson.fromJson(data, clazz);
	}

	@Override
	public <T> T getObject(byte[] data, Class<T> clazz) {
		return gson.fromJson(new String(data), clazz);
	}

	@Override
	public String getString(Object object) {
		return gson.toJson(object);
	}

	@Override
	public byte[] getBytes(Object object) {
		return gson.toJson(object).getBytes();
	}

}
