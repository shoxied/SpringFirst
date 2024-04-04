package org.example.csv;

import java.io.IOException;
import java.io.InputStream;

public interface DetailCsvReader {
    void read(InputStream inputStream) throws IOException;
}
