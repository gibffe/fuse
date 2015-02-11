package com.sulaco.fuse;

import java.util.concurrent.FutureTask;

public interface FuseServer {

    public FutureTask<Integer> startServer();

}
