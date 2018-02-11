package com.mccabe;

import com.mccabe.inst.Instrument;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo( name = "MccabeWork")
public class MccabeWork extends AbstractMojo {
    private static final String INST = "instrument";
    private static final String REPORT = "report";
    @Parameter(property = "type",defaultValue = "instrument")
    private String type;
    @Parameter(property = "propertyPath",defaultValue = "/usr/mccabe/helper/mccabe.properties")
    private String propertyPath;

    public void execute() throws MojoExecutionException {
        System.out.println("[execute : " + type + "]\n" + "[path : " + propertyPath + "]");
        try {
            switch (type) {
                case INST :
                    Instrument instrument = new Instrument();
                    break;
                case REPORT :
                    break;
            }
        } catch (Exception e) {
            throw new MojoExecutionException("error", e);
        }
    }
}
