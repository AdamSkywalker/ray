package org.skywind.ray.reflection;

import org.skywind.ray.meta.InterfaceAudience;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;

/**
 * Author: Sergey Saiyan sergey.sova42@gmail.com
 * Date: 30.06.2015 21:58
 */
@InterfaceAudience.Private
public class JarInputFile implements Scanner.File {
    private final ZipEntry entry;
    private final JarInputDir jarInputDir;
    private final long fromIndex;
    private final long endIndex;

    public JarInputFile(ZipEntry entry, JarInputDir jarInputDir, long cursor, long nextCursor) {
        this.entry = entry;
        this.jarInputDir = jarInputDir;
        fromIndex = cursor;
        endIndex = nextCursor;
    }

    @Override
    public String getName() {
        String name = entry.getName();
        return name.substring(name.lastIndexOf("/") + 1);
    }

    @Override
    public String getRelativePath() {
        return entry.getName();
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return new InputStream() {
            @Override
            public int read() throws IOException {
                if (jarInputDir.cursor >= fromIndex && jarInputDir.cursor <= endIndex) {
                    int read = jarInputDir.jarInputStream.read();
                    jarInputDir.cursor++;
                    return read;
                } else {
                    return -1;
                }
            }
        };
    }
}