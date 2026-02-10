package com.seanshubin.kotlin.reusable.di.contract;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface ProcessBuilderContract {
    ProcessBuilderContract command(List<String> command);

    ProcessBuilderContract command(String... command);

    List<String> command();

    ProcessBuilderContract directory(File directory);

    File directory();

    Map<String, String> environment();

    ProcessBuilderContract redirectErrorStream(boolean redirectErrorStream);

    boolean redirectErrorStream();

    ProcessContract start();
}
