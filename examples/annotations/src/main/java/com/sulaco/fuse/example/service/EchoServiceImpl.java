package com.sulaco.fuse.example.service;

import org.springframework.stereotype.Component;

@Component
public class EchoServiceImpl implements EchoService {

    @Override
    public String echo(String value) {
        return value.toUpperCase();
    }
}
