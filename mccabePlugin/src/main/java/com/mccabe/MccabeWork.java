package com.mccabe;

import com.mccabe.inst.Instrument;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.Properties;

@Mojo( name = "MccabeWork")
public class MccabeWork extends AbstractMojo {
    private static final String INST = "instrument";
    private static final String REPORT = "report";
    @Parameter(property = "type",defaultValue = "instrument")
    private String type;
    // properties
    @Parameter(property = "MCCABE_HOME")
    private String MCCABE_HOME;
    @Parameter(property = "programName")
    private String programName;
    @Parameter(property = "SRC_DIR")
    private String SRC_DIR;
    @Parameter(property = "PROJECT_DIR")
    private String PROJECT_DIR;
    @Parameter(property = "MCCABE_BIN")
    private String MCCABE_BIN;
    @Parameter(property = "INSTRUMENTED_SRC_DIR")
    private String INSTRUMENTED_SRC_DIR;
    @Parameter(property = "REPORT_DIR")
    private String REPORT_DIR;
    @Parameter(property = "TRACEFILE_HOME")
    private String TRACEFILE_HOME;

    public void execute() throws MojoExecutionException {
        try {
            switch (type) {
                case INST :
                    Instrument instrument = new Instrument();
                    instrument.checkAndSetProperties(makeProperties());
                    instrument.start();
                    break;
                case REPORT :
                    break;
            }
        } catch (Exception e) {
            throw new MojoExecutionException("error", e);
        }
    }

    private Properties makeProperties() {
        Properties properties = new Properties();
        setProperty(properties, "MCCABE_HOME", MCCABE_HOME);
        setProperty(properties, "MCCABE_BIN", MCCABE_BIN);
        setProperty(properties, "programName", programName);
        setProperty(properties, "SRC_DIR", SRC_DIR);
        setProperty(properties, "PROJECT_DIR", PROJECT_DIR);
        setProperty(properties, "INSTRUMENTED_SRC_DIR", INSTRUMENTED_SRC_DIR);
        setProperty(properties, "REPORT_DIR", REPORT_DIR);
        setProperty(properties, "TRACEFILE_HOME", TRACEFILE_HOME);
        return properties;
    }

    private void setProperty(Properties properties, String key, String val) {
        if (val == null)
            return;
        properties.setProperty(key, val);
    }
}