package com.testing_exam_webapp.service;

import com.testing_exam_webapp.dto.HospitalRequest;
import com.testing_exam_webapp.exception.EntityNotFoundException;
import com.testing_exam_webapp.model.mysql.Hospital;
import com.testing_exam_webapp.model.mysql.Ward;
import com.testing_exam_webapp.repository.HospitalRepository;
import com.testing_exam_webapp.repository.WardRepository;
import com.testing_exam_webapp.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Comprehensive test suite for HospitalService.
 * Tests CRUD operations and query methods with boundary analysis.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("HospitalService Tests")
class HospitalServiceTest {

    @Mock
    private HospitalRepository hospitalRepository;

    @Mock
    private WardRepository wardRepository;

    @InjectMocks
    private HospitalService hospitalService;

    private Hospital testHospital;
    private Ward testWard;

    @BeforeEach
    void setUp() {
        testHospital = TestDataBuilder.createHospital();
        testWard = TestDataBuilder.createWard();
    }

    @Test
    @DisplayName("getHospitals - Should return empty list")
    void getHospitals_EmptyList_ReturnsEmptyList() {
        when(hospitalRepository.findAll()).thenReturn(Collections.emptyList());

        List<Hospital> result = hospitalService.getHospitals();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getHospitalById - Should return hospital for valid ID")
    void getHospitalById_ValidId_ReturnsHospital() {
        UUID hospitalId = testHospital.getHospitalId();
        when(hospitalRepository.findById(hospitalId)).thenReturn(Optional.of(testHospital));

        Hospital result = hospitalService.getHospitalById(hospitalId);

        assertNotNull(result);
        assertEquals(hospitalId, result.getHospitalId());
    }

    @Test
    @DisplayName("getHospitalById - Should throw exception for null ID")
    void getHospitalById_NullId_ThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            hospitalService.getHospitalById(null);
        });
    }

    @Test
    @DisplayName("createHospital - Should create hospital with valid request")
    void createHospital_ValidRequest_CreatesHospital() {
        HospitalRequest request = new HospitalRequest();
        request.setHospitalName("New Hospital");
        request.setAddress("123 Main St");
        request.setCity("New City");

        when(hospitalRepository.save(any(Hospital.class))).thenAnswer(invocation -> {
            Hospital h = invocation.getArgument(0);
            h.setHospitalId(UUID.randomUUID());
            return h;
        });

        Hospital result = hospitalService.createHospital(request);

        assertNotNull(result);
        assertEquals("New Hospital", result.getHospitalName());
        assertEquals("New City", result.getCity());
    }

    @Test
    @DisplayName("createHospital - Should create hospital with wards")
    void createHospital_WithWards_CreatesHospital() {
        HospitalRequest request = new HospitalRequest();
        request.setHospitalName("New Hospital");
        request.setAddress("123 Main St");
        request.setCity("New City");
        request.setWardIds(Set.of(testWard.getWardId()));

        when(wardRepository.findById(testWard.getWardId())).thenReturn(Optional.of(testWard));
        when(hospitalRepository.save(any(Hospital.class))).thenAnswer(invocation -> {
            Hospital h = invocation.getArgument(0);
            h.setHospitalId(UUID.randomUUID());
            return h;
        });

        Hospital result = hospitalService.createHospital(request);

        assertNotNull(result);
        verify(wardRepository, times(1)).findById(testWard.getWardId());
    }

    @Test
    @DisplayName("createHospital - Should throw exception when ward not found")
    void createHospital_WardNotFound_ThrowsException() {
        UUID nonExistentWardId = UUID.randomUUID();
        HospitalRequest request = new HospitalRequest();
        request.setHospitalName("New Hospital");
        request.setWardIds(Set.of(nonExistentWardId));

        when(wardRepository.findById(nonExistentWardId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            hospitalService.createHospital(request);
        });
        assertTrue(exception.getMessage().contains("Ward not found"));
    }

    @Test
    @DisplayName("updateHospital - Should update hospital with valid request")
    void updateHospital_ValidRequest_UpdatesHospital() {
        UUID hospitalId = testHospital.getHospitalId();
        HospitalRequest request = new HospitalRequest();
        request.setHospitalName("Updated Hospital");
        request.setAddress("Updated Address");
        request.setCity("Updated City");

        when(hospitalRepository.findById(hospitalId)).thenReturn(Optional.of(testHospital));
        when(hospitalRepository.save(any(Hospital.class))).thenReturn(testHospital);

        Hospital result = hospitalService.updateHospital(hospitalId, request);

        assertNotNull(result);
        verify(hospitalRepository, times(1)).save(testHospital);
    }

    @Test
    @DisplayName("deleteHospital - Should delete hospital when valid ID provided")
    void deleteHospital_ValidId_DeletesHospital() {
        UUID hospitalId = testHospital.getHospitalId();
        when(hospitalRepository.existsById(hospitalId)).thenReturn(true);
        doNothing().when(hospitalRepository).deleteById(hospitalId);

        hospitalService.deleteHospital(hospitalId);

        verify(hospitalRepository, times(1)).deleteById(hospitalId);
    }

    @Test
    @DisplayName("getHospitalsByCity - Should return hospitals for valid city")
    void getHospitalsByCity_ValidCity_ReturnsHospitals() {
        String city = "Copenhagen";
        List<Hospital> hospitals = Arrays.asList(testHospital);
        when(hospitalRepository.findByCity(city)).thenReturn(hospitals);

        List<Hospital> result = hospitalService.getHospitalsByCity(city);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getHospitalsByCity - Should throw exception when null city provided")
    void getHospitalsByCity_NullCity_ThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            hospitalService.getHospitalsByCity(null);
        });
    }

    @Test
    @DisplayName("getHospitalsByCity - Should return empty list when no hospitals in city")
    void getHospitalsByCity_NoHospitals_ReturnsEmptyList() {
        String city = "Unknown City";
        when(hospitalRepository.findByCity(city)).thenReturn(Collections.emptyList());

        List<Hospital> result = hospitalService.getHospitalsByCity(city);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}

