package com.seanshubin.kotlin.reusable.di.contract;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ProcessBuilderContractChecked {
    ProcessBuilderContractChecked command(List<String> command);

    ProcessBuilderContractChecked command(String... command);

    List<String> command();

    ProcessBuilderContractChecked directory(File directory);

    File directory();

    Map<String, String> environment();

    ProcessBuilderContractChecked redirectErrorStream(boolean redirectErrorStream);

    boolean redirectErrorStream();

    ProcessContractChecked start() throws IOException;
}
