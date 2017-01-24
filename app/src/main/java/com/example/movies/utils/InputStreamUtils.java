package com.example.movies.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class InputStreamUtils {

	public static String toString(InputStream istream) throws IOException {
		Writer writer = new StringWriter();
		char[] buffer = new char[8192];
		Reader reader = new BufferedReader(new InputStreamReader(istream, "UTF-8"));
		int n;
		while ((n = reader.read(buffer)) != -1) {
			writer.write(buffer, 0, n);
		}
		return writer.toString();
	}

	public static void close(InputStream istream) {
		try {
			if (istream != null) {
				istream.close();
			}
		} catch (IOException e) {
			Log.w(Log.DEFAULT_TAG, "Failed to close stream", e);
		}
	}

}
