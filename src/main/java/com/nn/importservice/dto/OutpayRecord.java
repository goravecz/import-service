package com.nn.importservice.dto;

import java.math.BigDecimal;

public record OutpayRecord(
        String clntnum,
        String chdrnum,
        String letterType,
        String printDate,
        String dataId,
        String clntName,
        String clntAddress,
        String regDate,
        BigDecimal benPercent,
        String role1,
        String role2,
        String cownNum,
        String cownName
) {
}
