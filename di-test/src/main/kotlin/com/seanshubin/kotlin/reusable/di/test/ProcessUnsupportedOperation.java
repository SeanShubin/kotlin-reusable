package com.seanshubin.kotlin.reusable.di.test;

import com.seanshubin.kotlin.reusable.di.contract.ProcessContract;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

public class ProcessUnsupportedOperation implements ProcessContract {
    @Override
    public OutputStream getOutputStream() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public InputStream getInputStream() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public InputStream getErrorStream() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public int waitFor() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public boolean waitFor(long timeout, TimeUnit unit) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public int exitValue() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public void destroy() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public ProcessContract destroyForcibly() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public boolean isAlive() {
        throw new UnsupportedOperationException("Not Implemented!");
    }
}
