package indyscala.aug2013.service;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;

/**
 * Thanks, Mark Butler @ http://stackoverflow.com/a/15332031.
 *
 * It's not worth porting you to Scala.
 *
 * @author Ross A. Baker <baker@alumni.indiana.edu>
 */
public class CybozuLangDetectorYakShaver {
    public static void shave() throws IOException, LangDetectException {
        String dirname = "profiles.sm";
        Enumeration<URL> en = Detector.class.getClassLoader().getResources(dirname);
        List<String> profiles = new ArrayList<>();
        if (en.hasMoreElements()) {
            URL url = en.nextElement();
            JarURLConnection urlcon = (JarURLConnection) url.openConnection();
            try (JarFile jar = urlcon.getJarFile();) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    String entry = entries.nextElement().getName();
                    if (entry.startsWith(dirname)) {
                        try (InputStream in = Detector.class.getClassLoader()
                                .getResourceAsStream(entry);) {
                            profiles.add(IOUtils.toString(in));
                        }
                    }
                }
            }
        }
        DetectorFactory.loadProfile(profiles);
    }
}
