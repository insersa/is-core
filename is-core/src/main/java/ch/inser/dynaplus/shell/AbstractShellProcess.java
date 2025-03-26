/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package ch.inser.dynaplus.shell;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractShellProcess {

    private static final Log logger = LogFactory.getLog(AbstractShellProcess.class);

    private String iProcessPath;
    private String iLogFile;

    protected AbstractShellProcess(String aProcessPath, String aLogFile) {
        iProcessPath = aProcessPath;
        iLogFile = aLogFile;
    }

    public ShellResult execute() throws IOException, InterruptedException {
        logger.debug("Executing " + iProcessPath);
        Process theProcess = null;
        BufferedReader inStream = null;
        BufferedReader errorStream = null;

        theProcess = Runtime.getRuntime().exec(iProcessPath);

        inStream = new BufferedReader(new InputStreamReader(theProcess.getInputStream()));
        errorStream = new BufferedReader(new InputStreamReader(theProcess.getErrorStream()));

        // first read the errorStrean
        String sOutput = "";
        String sLineSep = System.getProperty("line.separator");

        boolean goAhead = true;
        int slept = 0;

        while (goAhead) {
            if (errorStream.ready()) {
                sOutput += errorStream.readLine() + sLineSep;
                slept = 0;
            } else if (inStream.ready()) {
                sOutput += inStream.readLine() + sLineSep;
                slept = 0;
            } else {
                if (slept > 30) {
                    goAhead = false;
                } else {
                    Thread.sleep(100);
                    slept++;
                }
            }
        }
        inStream.close();
        errorStream.close();
        if (iLogFile != null) {
            try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(iLogFile)))) {
                out.writeBytes(sOutput);
                out.flush();
            }
        }
        return new ShellResult(theProcess.exitValue(), sOutput);
    }

}
