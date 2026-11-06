package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.CityObject;
import org.example.repository.CityObjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityObjectService {

    private final CityObjectRepository repository;

    public List<CityObject> findAll() {
        return repository.findAll();
    }

    public CityObject findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("CityObject not found with id: " + id));
    }

    public CityObject create(CityObject cityObject) {
        return repository.save(cityObject);
    }

    public CityObject update(Long id, CityObject details) {
        CityObject cityObject = findById(id);
        cityObject.setAddress(details.getAddress());
        cityObject.setType(details.getType());
        cityObject.setCondition(details.getCondition());
        return repository.save(cityObject);
    }

    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }
}