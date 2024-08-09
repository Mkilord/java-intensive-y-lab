package autoservice.adapter.service.impl;

import autoservice.model.AuditAction;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for logging and managing audit logs.
 */
public class AuditService {
    private static final List<String> auditLogs = new ArrayList<>();

    /**
     * Logs an action performed by a user.
     *
     * @param username    the username of the user who performed the action
     * @param auditAction the action that was performed
     */
    public static void logAction(String username, AuditAction auditAction) {
        String logEntry = new Date() + " - User: " + username + " - Action: " + auditAction;
        auditLogs.add(logEntry);
    }

    public static void logAction(String username, AuditAction auditAction, String info) {
        String logEntry = new Date() + " - User: " + username + " - Action: " + auditAction + "\nInfo: "
                + info;
        auditLogs.add(logEntry);
    }

    /**
     * Returns all audit logs.
     *
     * @return a list of all audit logs
     */
    public static List<String> viewLogs() {
        return auditLogs;
    }

    /**
     * Filters audit logs by a given search string.
     *
     * @param searchString the string to filter logs by
     * @return a list of audit logs that contain the search string
     */
    public static List<String> filterLogsByString(String searchString) {
        return auditLogs.stream()
                .filter(log -> log.contains(searchString))
                .collect(Collectors.toList());
    }

    /**
     * Exports the audit logs to a file at the specified path.
     *
     * @param filePath the path to the file where logs will be exported
     * @throws IOException if an I/O error occurs
     */
    public static void exportLogs(String filePath) throws IOException {
        var path = Path.of("C:\\Users\\" + System.getProperty("user.name")).resolve(filePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toString()))) {
            for (String log : auditLogs) {
                writer.write(log);
                writer.newLine();
            }
        }
    }
}
