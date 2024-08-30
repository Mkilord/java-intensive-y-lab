package autoservice.adapter.service;

import autoservice.adapter.service.impl.AuditService;
import autoservice.domen.model.AuditAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AuditServiceTest {

    @BeforeEach
    public void setUp() {
        AuditService.viewLogs().clear();
    }

    @Test
    public void testLogActionWithoutInfo() {
        String username = "user1";
        AuditAction action = AuditAction.LOG_IN;

        AuditService.logAction(username, action);

        List<String> logs = AuditService.viewLogs();
        assertEquals(1, logs.size());
        assertTrue(logs.get(0).contains(username));
        assertTrue(logs.get(0).contains(action.toString()));
    }

    @Test
    public void testLogActionWithInfo() {
        String username = "user2";
        AuditAction action = AuditAction.EDIT_CAR;
        String info = "Changed the car color.";

        AuditService.logAction(username, action, info);

        List<String> logs = AuditService.viewLogs();
        assertEquals(1, logs.size());
        assertTrue(logs.get(0).contains(username));
        assertTrue(logs.get(0).contains(action.toString()));
        assertTrue(logs.get(0).contains(info));
    }

    @Test
    public void testFilterLogsByString() {
        String username = "user3";
        AuditAction action1 = AuditAction.ADD_CAR;
        AuditAction action2 = AuditAction.CANCEL_ORDER;
        AuditService.logAction(username, action1);
        AuditService.logAction(username, action2, "Order cancelled due to payment issue.");

        List<String> filteredLogs = AuditService.filterLogsByString(action1.toString());

        assertEquals(1, filteredLogs.size());
        String log = filteredLogs.get(0);
        assertTrue(log.contains(action1.toString()));
        assertFalse(log.contains(action2.toString()));
    }


    @Test
    public void testExportLogs() throws IOException {
        String username = "user4";
        AuditAction action = AuditAction.CREATE_ORDER;
        AuditService.logAction(username, action);

        Path tempFile = Files.createTempFile("audit_logs", ".txt");
        String filePath = tempFile.toString();

        AuditService.exportLogs(filePath);

        List<String> logsFromFile = Files.readAllLines(tempFile);
        assertEquals(1, logsFromFile.size());
        assertTrue(logsFromFile.get(0).contains(username));
        assertTrue(logsFromFile.get(0).contains(action.toString()));

        Files.delete(tempFile);
    }
}
