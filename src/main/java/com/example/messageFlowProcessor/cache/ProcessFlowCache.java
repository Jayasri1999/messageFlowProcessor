package com.example.messageFlowProcessor.cache;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.messageFlowProcessor.entity.ProcessFlow;
import com.example.messageFlowProcessor.repository.ProcessFlowRepository;



@Component
public class ProcessFlowCache {
	@Autowired
	private ProcessFlowRepository processFlowRepository;

    private final Map<String, ProcessFlow> cache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public ProcessFlowCache() {
        // Schedule cache refresh every 1 hour
        scheduler.scheduleAtFixedRate(this::refreshCache, 0, 1, TimeUnit.HOURS);
    }

    public ProcessFlow getProcessFlow(String scenario, String country, String instance) {
        String key = generateKey(scenario, country, instance);
        ProcessFlow processFlow = cache.get(key);
        System.out.println("+++++++++++Process Flow+++++++++++++++ "+processFlow);
        if (processFlow == null) {
            // Fetch from DB if not in cache
        	System.out.println("+++++++++++Data is not in cache+++++++++++++++ ");
        	processFlow = processFlowRepository.findByKeyCountryInstance(scenario, country, instance)
    				.orElseThrow(() -> new IllegalArgumentException("ProcessFlow not found"));
            if (processFlow != null) {
                cache.put(key, processFlow);
            }
            
        }

        return processFlow;
    }

    private void refreshCache() {
        cache.clear();
    }
    private static String generateKey(String scenario, String country, String instance) {
        return scenario + "." + country + "." + instance;
    }
}