package com.seanshubin.kotlin.reusable.di.contract;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

public interface ProcessContractChecked {
    OutputStream getOutputStream();

    InputStream getInputStream();

    InputStream getErrorStream();

    int waitFor() throws InterruptedException;

    boolean waitFor(long timeout, TimeUnit unit) throws InterruptedException;

    int exitValue();

    void destroy();

    ProcessContractChecked destroyForcibly();

    boolean isAlive();
}
