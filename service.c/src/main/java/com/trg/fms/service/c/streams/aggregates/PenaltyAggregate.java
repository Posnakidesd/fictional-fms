package com.trg.fms.service.c.streams.aggregates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.trg.fms.api.Heartbeat;

@JsonIgnoreProperties(ignoreUnknown=true)
public class PenaltyAggregate {

    private Long total60Km;
    private Long total80Km;

    public PenaltyAggregate() {
        this.total60Km = 0L;
        this.total80Km = 0L;
    }

    public PenaltyAggregate copy(PenaltyAggregate other) {
        this.total60Km += other.getTotal60Km();
        this.total80Km += other.getTotal80Km();
        return this;
    }

    public PenaltyAggregate withHeartbeat(Heartbeat heartbeat) {

        switch (heartbeat.getType()) {
            case TYPE60:
                this.total60Km += heartbeat.getDistance();
                break;
            case TYPE80:
                this.total80Km += heartbeat.getDistance();
                break;
            default:
        }
        return this;
    }

    public Long calculatePoints() {
        long totalPoint = this.total60Km * 2;
        totalPoint += this.total80Km * 5;
        return totalPoint;
    }

    public Long getTotal60Km() {
        return this.total60Km;
    }

    public void setTotal60Km(Long total60Km) {
        this.total60Km = total60Km;
    }

    public Long getTotal80Km() {
        return total80Km;
    }

    public void setTotal80Km(Long total80Km) {
        this.total80Km = total80Km;
    }

}