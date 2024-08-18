package autoservice.adapter.service;

import autoservice.adapter.service.impl.AuditService;
import autoservice.model.AuditAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuditServiceTest {

    @BeforeEach
    void setUp() {
        // Clear logs before each test
        AuditService.viewLogs().clear();
    }

    @Test
    void testLogAction() {
        AuditService.logAction("johndoe", AuditAction.LOG_IN);
        List<String> logs = AuditService.viewLogs();
        assertEquals(1, logs.size(), "There should be 1 log entry");
        assertTrue(logs.get(0).contains("User: johndoe - Action: Login"), "Log entry should match the expected format");
    }

    @Test
    void testFilterLogsByString() {
        AuditService.logAction("johndoe", AuditAction.LOG_IN);
        AuditService.logAction("janesmith", AuditAction.LOG_IN);

        List<String> filteredLogs = AuditService.filterLogsByString("Login");
        assertEquals(1, filteredLogs.size(), "There should be 1 log entry containing 'Login'");
        assertTrue(filteredLogs.get(0).contains("User: johndoe - Action: Login"), "Filtered log entry should match the expected format");
    }

    @Test
    void testExportLogs() throws IOException {
        AuditService.logAction("johndoe", AuditAction.LOG_IN);
        AuditService.logAction("janesmith", AuditAction.LOG_IN);

        Path tempFile = Files.createTempFile("audit_logs", ".txt");
        AuditService.exportLogs(tempFile.toString());

        List<String> fileContent = Files.readAllLines(tempFile);
        assertEquals(2, fileContent.size(), "Exported file should contain 2 log entries");
        assertTrue(fileContent.get(0).contains("User: johndoe - Action: Login"), "First line of the file should match the expected format");
        assertTrue(fileContent.get(1).contains("User: janesmith - Action: Logout"), "Second line of the file should match the expected format");

        // Clean up the temporary file after the test
        Files.deleteIfExists(tempFile);
    }
}
