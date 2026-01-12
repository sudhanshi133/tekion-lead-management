package com.tekion.demo.port;

import com.tekion.demo.lead.Lead;
import com.tekion.demo.lead.LeadState;
import java.util.List;
import java.util.Optional;

public interface LeadPersistencePort {

    Lead save(Lead lead);

    Optional<Lead> findByIdAndDealerId(String leadId, String dealerId);

    List<Lead> findByDealerIdAndState(String dealerId, LeadState state);

    List<Lead> findByDealerIdOrderByScore(String dealerId, int limit);
}
