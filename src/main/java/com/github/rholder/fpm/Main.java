/*
 * Copyright 2014-2015 Ray Holder
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.rholder.fpm;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static File EXTRACTED_GEM_JAR;

    public static void main(String[] args) throws IOException {

        // extract gem jar because injecting gems inside of jruby-complete is 10x slower to bootstrap ):
        EXTRACTED_GEM_JAR = File.createTempFile("ruby-gems", ".jar");
        EXTRACTED_GEM_JAR.deleteOnExit();

        extractGemFile(EXTRACTED_GEM_JAR);

        // munge the args to bootstrap running FPM with an external gem jar
        List<String> ar = new ArrayList<String>();
        ar.add("-r" + EXTRACTED_GEM_JAR.getAbsolutePath());
        ar.add("classpath:jar-bootstrap.rb");
        ar.addAll(Arrays.asList(args));

        org.jruby.Main.main(ar.toArray(new String[ar.size()]));
    }

    /**
     * Extract the gem file jar from the classpath and write it out to a
     * temporary file.
     *
     * @throws IOException
     */
    public static void extractGemFile(File gemFile) throws IOException {

        // extract gem file from the classpath
        InputStream in = Main.class.getResourceAsStream("/ruby-gems.jar");
        OutputStream out = new BufferedOutputStream(new FileOutputStream(gemFile));

        int n;
        byte[] buffer = new byte[4096];
        while (-1 != (n = in.read(buffer))) {
            out.write(buffer, 0, n);
        }
        in.close();
        out.close();
    }
}
