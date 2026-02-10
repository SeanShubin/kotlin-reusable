package com.seanshubin.kotlin.reusable.di.delegate;

import com.seanshubin.kotlin.reusable.di.contract.ProcessBuilderContract;
import com.seanshubin.kotlin.reusable.di.contract.ProcessBuilderContractChecked;
import com.seanshubin.kotlin.reusable.di.contract.ProcessContract;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ProcessBuilderDelegate implements ProcessBuilderContract {
    private final ProcessBuilderContractChecked delegate;

    public ProcessBuilderDelegate() {
        this.delegate = new ProcessBuilderDelegateChecked();
    }

    public ProcessBuilderDelegate(List<String> command) {
        this.delegate = new ProcessBuilderDelegateChecked(command);
    }

    public ProcessBuilderDelegate(String... command) {
        this.delegate = new ProcessBuilderDelegateChecked(command);
    }

    private ProcessBuilderDelegate(ProcessBuilderContractChecked delegate) {
        this.delegate = delegate;
    }

    @Override
    public ProcessBuilderContract command(List<String> command) {
        delegate.command(command);
        return this;
    }

    @Override
    public ProcessBuilderContract command(String... command) {
        delegate.command(command);
        return this;
    }

    @Override
    public List<String> command() {
        return delegate.command();
    }

    @Override
    public ProcessBuilderContract directory(File directory) {
        delegate.directory(directory);
        return this;
    }

    @Override
    public File directory() {
        return delegate.directory();
    }

    @Override
    public Map<String, String> environment() {
        return delegate.environment();
    }

    @Override
    public ProcessBuilderContract redirectErrorStream(boolean redirectErrorStream) {
        delegate.redirectErrorStream(redirectErrorStream);
        return this;
    }

    @Override
    public boolean redirectErrorStream() {
        return delegate.redirectErrorStream();
    }

    @Override
    public ProcessContract start() {
        try {
            return new ProcessDelegate(delegate.start());
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
}
