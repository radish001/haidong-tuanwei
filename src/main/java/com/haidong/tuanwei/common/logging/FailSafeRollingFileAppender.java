package com.haidong.tuanwei.common.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FailSafeRollingFileAppender extends RollingFileAppender<ILoggingEvent> {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private volatile boolean fileOutputAvailable;
    private volatile boolean fallbackAnnounced;

    @Override
    public void start() {
        try {
            super.start();
            fileOutputAvailable = super.isStarted();
            if (!fileOutputAvailable) {
                enableFallback("File logging is unavailable, falling back to no-op file appender.", null);
            }
        } catch (Exception ex) {
            enableFallback("File logging failed to start, falling back to no-op file appender.", ex);
        }
    }

    @Override
    public void stop() {
        fileOutputAvailable = false;
        super.stop();
    }

    @Override
    public void doAppend(ILoggingEvent eventObject) {
        if (!fileOutputAvailable) {
            return;
        }
        super.doAppend(eventObject);
    }

    @Override
    public void addError(String msg) {
        if (started) {
            enableFallback(msg, null);
            return;
        }
        announceFallback(msg, null);
    }

    @Override
    public void addError(String msg, Throwable ex) {
        if (started) {
            enableFallback(msg, ex);
            return;
        }
        announceFallback(msg, ex);
    }

    private void enableFallback(String message, Throwable ex) {
        fileOutputAvailable = false;
        started = true;
        announceFallback(message, ex);
    }

    private void announceFallback(String message, Throwable ex) {
        if (fallbackAnnounced) {
            return;
        }
        fallbackAnnounced = true;
        System.err.printf("%s [FAIL-SAFE-LOGGING] %s%n",
                LocalDateTime.now().format(TIMESTAMP_FORMATTER), message);
        if (ex != null) {
            ex.printStackTrace(System.err);
        }
    }
}
