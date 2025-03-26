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

package ch.inser.jsl.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author INSER SA
 * @version 1.0
 */
public class FileMonitor {

    private static final FileMonitor instance = new FileMonitor();

    private Timer timer;

    private Hashtable<String, FileMonitorTask> timerEntries;

    public static FileMonitor getInstance() {
        return instance;
    }

    protected FileMonitor() {
        // Create timer, run timer thread as daemon.
        timer = new Timer(true);
        timerEntries = new Hashtable<>();
    }

    /**
     * Add a monitored file with a FileChangeListener.
     *
     * @param listener
     *            listener to notify when the file changed.
     * @param fileName
     *            name of the file to monitor.
     * @param period
     *            polling period in milliseconds.
     */
    public void addFileChangeListener(FileChangeListener listener, String fileName, long period) throws FileNotFoundException {
        removeFileChangeListener(fileName);
        FileMonitorTask task = new FileMonitorTask(listener, fileName);
        timerEntries.put(fileName, task);
        timer.schedule(task, period, period);
    }

    /**
     * Remove the listener from the notification list.
     *
     * @param fileName
     */
    public void removeFileChangeListener(String fileName) {
        FileMonitorTask task = timerEntries.remove(fileName);
        if (task != null) {
            task.cancel();
        }
    }

    /**
     * Remove the listener from the notification list. A NullPointerException is thrown by this method !!!
     *
     * @param listener
     *            the listener to be removed.
     */
    public void removeFileChangeListener(FileChangeListener listener, String fileName) {
        FileMonitorTask task = timerEntries.remove(fileName + listener.hashCode());
        if (task != null) {
            task.cancel();
        }
    }

    protected void fireFileChangeEvent(FileChangeListener listener, String fileName) {
        listener.fileChanged(fileName);
    }

    class FileMonitorTask extends TimerTask {
        FileChangeListener listener;

        String fileName;

        File monitoredFile;

        long iLastModified;

        public FileMonitorTask(FileChangeListener aListener, String aFileName) throws FileNotFoundException {
            listener = aListener;
            fileName = aFileName;
            iLastModified = 0;

            monitoredFile = new File(aFileName);

            if (!monitoredFile.exists()) { // but is it on CLASSPATH?
                URL fileURL = aListener.getClass().getClassLoader().getResource(aFileName);

                if (fileURL != null) {
                    monitoredFile = new File(fileURL.getFile());
                } else {
                    throw new FileNotFoundException("File Not Found: " + aFileName);
                }
            }

            iLastModified = monitoredFile.lastModified();
        }

        @Override
        public void run() {
            long lastModified = monitoredFile.lastModified();

            if (lastModified != iLastModified) {
                iLastModified = lastModified;
                fireFileChangeEvent(listener, fileName);
            }
        }
    }
}