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
    @Parameter(property = "MCCABE_HOME", defaultValue = "/usr/mccabe")
    private String MCCABE_HOME;
    @Parameter(property = "programName", defaultValue = "default")
    private String programName;
    @Parameter(property = "SRC_DIR", defaultValue = "/usr/mccabe/src")
    private String SRC_DIR;
    @Parameter(property = "PROJECT_DIR", defaultValue = "/usr/mccabe/projects")
    private String PROJECT_DIR;


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
        properties.setProperty("MCCABE_HOME", MCCABE_HOME);
        properties.setProperty("programName", programName);
        properties.setProperty("SRC_DIR", SRC_DIR);
        properties.setProperty("PROJECT_DIR", PROJECT_DIR);
        return properties;
    }
}
