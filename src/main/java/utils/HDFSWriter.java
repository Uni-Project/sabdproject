package utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.net.URI;

public class HDFSWriter {

    public static void write(String json, String path) {

        Configuration conf = new Configuration();
        URI uri = URI.create(path);
        FileSystem fs = null;
        try {
            fs = FileSystem.get(uri,conf);
            FSDataOutputStream out = fs.create(new Path(uri));

            out.writeBytes(json);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
