package com.testing_exam_webapp.service;

import com.testing_exam_webapp.dto.WardRequest;
import com.testing_exam_webapp.model.mysql.Ward;
import com.testing_exam_webapp.model.types.WardType;
import com.testing_exam_webapp.repository.WardRepository;
import com.testing_exam_webapp.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Comprehensive test suite for WardService.
 * Demonstrates boundary value analysis for maxCapacity and equivalence partitioning for WardType.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("WardService Tests")
class WardServiceTest {

    @Mock
    private WardRepository wardRepository;

    @InjectMocks
    private WardService wardService;

    private Ward testWard;

    @BeforeEach
    void setUp() {
        testWard = TestDataBuilder.createWard();
    }

    @Test
    @DisplayName("getWards - Should return empty list")
    void getWards_EmptyList_ReturnsEmptyList() {
        when(wardRepository.findAll()).thenReturn(Collections.emptyList());

        List<Ward> result = wardService.getWards();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getWardById - Should return ward for valid ID")
    void getWardById_ValidId_ReturnsWard() {
        UUID wardId = testWard.getWardId();
        when(wardRepository.findById(wardId)).thenReturn(Optional.of(testWard));

        Ward result = wardService.getWardById(wardId);

        assertNotNull(result);
        assertEquals(wardId, result.getWardId());
    }

    @Test
    @DisplayName("getWardById - Should throw exception for null ID")
    void getWardById_NullId_ThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            wardService.getWardById(null);
        });
    }

    @Test
    @DisplayName("createWard - Should create ward with valid request")
    void createWard_ValidRequest_CreatesWard() {
        WardRequest request = new WardRequest();
        request.setType(WardType.NEUROLOGY);
        request.setMaxCapacity(25);

        when(wardRepository.save(any(Ward.class))).thenAnswer(invocation -> {
            Ward w = invocation.getArgument(0);
            w.setWardId(UUID.randomUUID());
            return w;
        });

        Ward result = wardService.createWard(request);

        assertNotNull(result);
        assertEquals(WardType.NEUROLOGY, result.getType());
        assertEquals(25, result.getMaxCapacity());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 1000})
    @DisplayName("createWard - Boundary Analysis: Max capacity boundary values")
    void createWard_MaxCapacityBoundaryValues_CreatesWard(int capacity) {
        WardRequest request = new WardRequest();
        request.setType(WardType.CARDIOLOGY);
        request.setMaxCapacity(capacity);

        when(wardRepository.save(any(Ward.class))).thenAnswer(invocation -> {
            Ward w = invocation.getArgument(0);
            w.setWardId(UUID.randomUUID());
            return w;
        });

        Ward result = wardService.createWard(request);
        assertEquals(capacity, result.getMaxCapacity());
    }

    @ParameterizedTest
    @EnumSource(WardType.class)
    @DisplayName("createWard - Equivalence Partitioning: Test all ward types")
    void createWard_AllWardTypes_CreatesWard(WardType type) {
        WardRequest request = new WardRequest();
        request.setType(type);
        request.setMaxCapacity(30);

        when(wardRepository.save(any(Ward.class))).thenAnswer(invocation -> {
            Ward w = invocation.getArgument(0);
            w.setWardId(UUID.randomUUID());
            return w;
        });

        Ward result = wardService.createWard(request);
        assertEquals(type, result.getType());
    }

    @Test
    @DisplayName("updateWard - Should update ward with valid request")
    void updateWard_ValidRequest_UpdatesWard() {
        UUID wardId = testWard.getWardId();
        WardRequest request = new WardRequest();
        request.setType(WardType.NEUROLOGY);
        request.setMaxCapacity(40);

        when(wardRepository.findById(wardId)).thenReturn(Optional.of(testWard));
        when(wardRepository.save(any(Ward.class))).thenReturn(testWard);

        Ward result = wardService.updateWard(wardId, request);

        assertNotNull(result);
        verify(wardRepository, times(1)).save(testWard);
    }

    @Test
    @DisplayName("deleteWard - Should delete ward when valid ID provided")
    void deleteWard_ValidId_DeletesWard() {
        UUID wardId = testWard.getWardId();
        when(wardRepository.existsById(wardId)).thenReturn(true);
        doNothing().when(wardRepository).deleteById(wardId);

        wardService.deleteWard(wardId);

        verify(wardRepository, times(1)).deleteById(wardId);
    }

    @Test
    @DisplayName("getWardsByType - Should return wards for valid type (Equivalence Partitioning)")
    void getWardsByType_ValidType_ReturnsWards() {
        WardType type = WardType.CARDIOLOGY;
        List<Ward> wards = Arrays.asList(testWard);
        when(wardRepository.findByType(type)).thenReturn(wards);

        List<Ward> result = wardService.getWardsByType(type);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getWardsByHospitalId - Should return wards for valid hospital ID")
    void getWardsByHospitalId_ValidHospitalId_ReturnsWards() {
        UUID hospitalId = UUID.randomUUID();
        List<Ward> wards = Arrays.asList(testWard);
        when(wardRepository.findByHospitalId(hospitalId)).thenReturn(wards);

        List<Ward> result = wardService.getWardsByHospitalId(hospitalId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getWardsByHospitalId - Should throw exception when null hospital ID provided")
    void getWardsByHospitalId_NullHospitalId_ThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            wardService.getWardsByHospitalId(null);
        });
    }
}

