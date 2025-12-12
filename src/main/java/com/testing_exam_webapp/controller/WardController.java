package com.testing_exam_webapp.controller;

import com.testing_exam_webapp.dto.WardRequest;
import com.testing_exam_webapp.model.mysql.Ward;
import com.testing_exam_webapp.model.types.WardType;
import com.testing_exam_webapp.service.WardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/wards")
public class WardController {

    private final WardService wardService;

    public WardController(WardService wardService) {
        this.wardService = wardService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<Ward>> getWards() {
        List<Ward> wards = wardService.getWards();
        if(wards.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(wards, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Ward> getWardById(@PathVariable UUID id) {
        Ward ward = wardService.getWardById(id);
        return new ResponseEntity<>(ward, HttpStatus.OK);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Ward> createWard(@Valid @RequestBody WardRequest request) {
        Ward ward = wardService.createWard(request);
        return new ResponseEntity<>(ward, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Ward> updateWard(@PathVariable UUID id, @Valid @RequestBody WardRequest request) {
        Ward ward = wardService.updateWard(id, request);
        return new ResponseEntity<>(ward, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteWard(@PathVariable UUID id) {
        wardService.deleteWard(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Query endpoints
    @GetMapping("/by-type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<Ward>> getWardsByType(@PathVariable WardType type) {
        List<Ward> wards = wardService.getWardsByType(type);
        if (wards.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(wards, HttpStatus.OK);
    }

    @GetMapping("/by-hospital/{hospitalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<Ward>> getWardsByHospitalId(@PathVariable UUID hospitalId) {
        List<Ward> wards = wardService.getWardsByHospitalId(hospitalId);
        if (wards.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(wards, HttpStatus.OK);
    }
}

