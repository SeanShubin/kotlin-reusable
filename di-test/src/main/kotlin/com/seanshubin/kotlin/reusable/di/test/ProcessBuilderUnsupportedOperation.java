package com.seanshubin.kotlin.reusable.di.test;

import com.seanshubin.kotlin.reusable.di.contract.ProcessBuilderContract;
import com.seanshubin.kotlin.reusable.di.contract.ProcessContract;

import java.io.File;
import java.util.List;
import java.util.Map;

public class ProcessBuilderUnsupportedOperation implements ProcessBuilderContract {
    @Override
    public ProcessBuilderContract command(List<String> command) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public ProcessBuilderContract command(String... command) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public List<String> command() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public ProcessBuilderContract directory(File directory) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public File directory() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Map<String, String> environment() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public ProcessBuilderContract redirectErrorStream(boolean redirectErrorStream) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public boolean redirectErrorStream() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public ProcessContract start() {
        throw new UnsupportedOperationException("Not Implemented!");
    }
}
