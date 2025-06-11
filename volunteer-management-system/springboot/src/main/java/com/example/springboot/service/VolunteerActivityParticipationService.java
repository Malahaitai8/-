// 文件路径: com/example/springboot/service/VolunteerActivityParticipationService.java

package com.example.springboot.service;

import com.example.springboot.mapper.VolunteerActivityParticipationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class VolunteerActivityParticipationService {

    @Autowired
    private VolunteerActivityParticipationMapper participationMapper;

    public List<Map<String, Object>> findMyParticipations(String volunteerId) {
        return participationMapper.selectMyParticipations(volunteerId);
    }

    public void rateOrganization(String volunteerId, String actualPositionId, Integer rating) {
        participationMapper.updateVolunteerToOrgRating(volunteerId, actualPositionId, rating);
    }
}