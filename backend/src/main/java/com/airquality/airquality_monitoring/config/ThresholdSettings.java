package com.airquality.airquality_monitoring.config;

import org.springframework.stereotype.Component;

@Component
public class ThresholdSettings {
    public double getPm25() { return 25.0; }
    public double getPm10() { return 50.0; }
    public double getNo2() { return 40.0; }
    public double getSo2() { return 20.0; }
    public double getO3() { return 100.0; }
}
