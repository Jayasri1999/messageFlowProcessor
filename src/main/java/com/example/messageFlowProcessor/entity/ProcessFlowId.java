package com.example.messageFlowProcessor.entity;

import java.io.Serializable;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProcessFlowId implements Serializable{
	private String scenario;
    private String country;
    private String instance;
    public ProcessFlowId() {}

    public ProcessFlowId(String scenario, String country, String instance) {
        this.scenario = scenario;
        this.country = country;
        this.instance = instance;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessFlowId that = (ProcessFlowId) o;
        return instance == that.instance && Objects.equals(scenario, that.scenario) && Objects.equals(country, that.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scenario, country, instance);
    }
}
