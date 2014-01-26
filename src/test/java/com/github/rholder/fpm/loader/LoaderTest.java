/*
 * Copyright 2014 Ray Holder
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

package com.github.rholder.fpm.loader;

import com.github.rholder.fpm.Main;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class LoaderTest {


    /**
     * Let's sanity check the behavior of a cached index file.
     *
     * @throws IOException
     */
    @Ignore
    @Test
    public void sanityCheckIndex() throws IOException {
        Map<String, String> index = Main.indexGemFile(new File("lib/ruby-gems.jar"));
        for(Map.Entry<String, String> e : index.entrySet()) {
            String[] a = splitJarUrl(e.getValue());
            System.out.println(e.getKey() + ", " + e.getValue() + ", " + a[0] + ", " + a[1]);
        }
    }

    // mimic behavior of JRuby's jar path loading
    private String[] splitJarUrl(String loadPathEntry) {
        int idx = loadPathEntry.indexOf("!");
        if (idx == -1) {
            return new String[]{loadPathEntry, ""};
        }

        String filename = loadPathEntry.substring(0, idx);
        String entry = idx + 2 < loadPathEntry.length() ? loadPathEntry.substring(idx + 2) : "";

        if(filename.startsWith("jar:")) {
            filename = filename.substring(4);
        }

        if(filename.startsWith("file:")) {
            filename = filename.substring(5);
        }

        return new String[]{filename, entry};
    }
}
