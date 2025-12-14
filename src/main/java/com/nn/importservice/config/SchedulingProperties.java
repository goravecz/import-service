package com.nn.importservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "scheduling")
public record SchedulingProperties(
    String redemptionImportCron,
    String outpayImportCron,
    String ownAndBenImportCron
) {}
