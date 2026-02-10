package com.seanshubin.kotlin.reusable.di.contract;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

public interface ProcessContract {
    OutputStream getOutputStream();

    InputStream getInputStream();

    InputStream getErrorStream();

    int waitFor();

    boolean waitFor(long timeout, TimeUnit unit);

    int exitValue();

    void destroy();

    ProcessContract destroyForcibly();

    boolean isAlive();
}
