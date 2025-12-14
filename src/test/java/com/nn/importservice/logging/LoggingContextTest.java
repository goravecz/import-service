package com.nn.importservice.logging;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;

class LoggingContextTest {

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void testSetOperation_SetsServiceOperationAndCorrelationId() {
        LoggingContext.setOperation("TEST_OPERATION");

        assertEquals("import-service", MDC.get("service"));
        assertEquals("TEST_OPERATION", MDC.get("operation"));
        assertNotNull(MDC.get("correlationId"));
    }

    @Test
    void testSetOperation_DoesNotOverrideExistingCorrelationId() {
        String existingCorrelationId = "existing-correlation-id";
        LoggingContext.setCorrelationId(existingCorrelationId);

        LoggingContext.setOperation("TEST_OPERATION");

        assertEquals(existingCorrelationId, MDC.get("correlationId"));
    }

    @Test
    void testSetFileContext_SetsFileTypeAndFileName() {
        LoggingContext.setFileContext("REDEMPTION", "redemption_001.txt");

        assertEquals("REDEMPTION", MDC.get("fileType"));
        assertEquals("redemption_001.txt", MDC.get("fileName"));
    }

    @Test
    void testClear_RemovesAllMDCValues() {
        LoggingContext.setOperation("TEST_OPERATION");
        LoggingContext.setFileContext("REDEMPTION", "redemption_001.txt");

        LoggingContext.clear();

        assertNull(MDC.get("service"));
        assertNull(MDC.get("operation"));
        assertNull(MDC.get("fileType"));
        assertNull(MDC.get("fileName"));
        assertNull(MDC.get("correlationId"));
    }

    @Test
    void testGetCorrelationId_ReturnsSetValue() {
        String correlationId = "test-correlation-id";
        LoggingContext.setCorrelationId(correlationId);

        assertEquals(correlationId, LoggingContext.getCorrelationId());
    }

    @Test
    void testGenerateCorrelationId_ReturnsUUID() {
        LoggingContext.setOperation("TEST_OPERATION");
        String correlationId = LoggingContext.getCorrelationId();

        assertNotNull(correlationId);
        // UUID format check (simple validation)
        assertTrue(correlationId.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
    }

    @Test
    void testSetFileContext_WithNullValues() {
        LoggingContext.setFileContext(null, null);

        assertNull(MDC.get("fileType"));
        assertNull(MDC.get("fileName"));
    }
}
