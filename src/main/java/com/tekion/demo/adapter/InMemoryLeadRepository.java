package com.tekion.demo.adapter;

import com.tekion.demo.lead.Lead;
import com.tekion.demo.lead.LeadState;
import com.tekion.demo.port.LeadPersistencePort;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryLeadRepository implements LeadPersistencePort {

    private final Map<String, Map<String, Lead>> storage = new ConcurrentHashMap<>();

    @Override
    public Lead save(Lead lead) {
        storage.computeIfAbsent(lead.getDealerId(), k -> new HashMap<>())
                .put(lead.getLeadId(), lead);
        return lead;
    }

    @Override
    public Optional<Lead> findByIdAndDealerId(String leadId, String dealerId) {
        return Optional.ofNullable(storage.getOrDefault(dealerId, Collections.emptyMap()).get(leadId));
    }

    @Override
    public List<Lead> findByDealerIdAndState(String dealerId, LeadState state) {
        return storage.getOrDefault(dealerId, Collections.emptyMap()).values().stream()
                .filter(l -> l.getState() == state).collect(Collectors.toList());
    }

    @Override
    public List<Lead> findByDealerIdOrderByScore(String dealerId, int limit) {
        return storage.getOrDefault(dealerId, Collections.emptyMap()).values().stream()
                .limit(limit).collect(Collectors.toList());
    }
}
