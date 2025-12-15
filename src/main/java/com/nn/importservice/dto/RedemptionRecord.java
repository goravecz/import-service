package com.nn.importservice.dto;

import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.annotations.Trim;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedemptionRecord {
    
    @Parsed(index = 0)
    @Trim
    private String company;
    
    @Parsed(index = 1)
    @Trim
    private String chdrnum;
    
    @Parsed(index = 2)
    @Trim
    private BigDecimal survalue;
    
    @Parsed(index = 3)
    @Trim
    private String jobUser;
    
    @Parsed(index = 4)
    @Trim
    private String jobName;
    
    @Parsed(index = 5)
    @Trim
    private String jobTimestamp;
    
    public RedemptionRecord(String company, String chdrnum, BigDecimal survalue, String jobUser, String jobName) {
        this.company = company;
        this.chdrnum = chdrnum;
        this.survalue = survalue;
        this.jobUser = jobUser;
        this.jobName = jobName;
    }
}
