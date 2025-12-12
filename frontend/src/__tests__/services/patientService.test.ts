import { describe, it, expect, vi, beforeEach } from 'vitest';
import { patientService } from '../../services/patientService';
import api from '../../services/api';
import { Patient, PatientRequest } from '../../types';

// Mock the api module
vi.mock('../../services/api', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

describe('patientService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('getAll', () => {
    it('should fetch all patients successfully', async () => {
      const mockPatients: Patient[] = [
        {
          patientId: '1',
          patientName: 'John Doe',
          dateOfBirth: '1990-01-01',
          gender: 'Male',
        },
      ];

      (api.get as any).mockResolvedValue({ data: mockPatients });

      const result = await patientService.getAll();

      expect(result).toEqual(mockPatients);
      expect(api.get).toHaveBeenCalledWith('/patients/all');
    });

    it('should handle errors when fetching patients', async () => {
      const error = new Error('Network error');
      (api.get as any).mockRejectedValue(error);

      await expect(patientService.getAll()).rejects.toThrow('Network error');
    });
  });

  describe('getById', () => {
    it('should fetch patient by id successfully', async () => {
      const mockPatient: Patient = {
        patientId: '1',
        patientName: 'John Doe',
        dateOfBirth: '1990-01-01',
        gender: 'Male',
      };

      (api.get as any).mockResolvedValue({ data: mockPatient });

      const result = await patientService.getById('1');

      expect(result).toEqual(mockPatient);
      expect(api.get).toHaveBeenCalledWith('/patients/1');
    });
  });

  describe('create', () => {
    it('should create patient successfully', async () => {
      const request: PatientRequest = {
        patientName: 'Jane Doe',
        dateOfBirth: '1995-05-15',
        gender: 'Female',
      };

      const mockPatient: Patient = {
        patientId: '2',
        ...request,
      };

      (api.post as any).mockResolvedValue({ data: mockPatient });

      const result = await patientService.create(request);

      expect(result).toEqual(mockPatient);
      expect(api.post).toHaveBeenCalledWith('/patients/create', request);
    });

    it('should handle validation errors', async () => {
      const request: PatientRequest = {
        patientName: '',
        dateOfBirth: '1995-05-15',
        gender: 'Female',
      };

      const error = new Error('Validation failed');
      (api.post as any).mockRejectedValue(error);

      await expect(patientService.create(request)).rejects.toThrow('Validation failed');
    });
  });

  describe('update', () => {
    it('should update patient successfully', async () => {
      const request: PatientRequest = {
        patientName: 'Updated Name',
        dateOfBirth: '1990-01-01',
        gender: 'Male',
      };

      const mockPatient: Patient = {
        patientId: '1',
        ...request,
      };

      (api.put as any).mockResolvedValue({ data: mockPatient });

      const result = await patientService.update('1', request);

      expect(result).toEqual(mockPatient);
      expect(api.put).toHaveBeenCalledWith('/patients/update/1', request);
    });
  });

  describe('delete', () => {
    it('should delete patient successfully', async () => {
      (api.delete as any).mockResolvedValue({});

      await patientService.delete('1');

      expect(api.delete).toHaveBeenCalledWith('/patients/delete/1');
    });

    it('should handle errors when deleting patient', async () => {
      const error = new Error('Delete failed');
      (api.delete as any).mockRejectedValue(error);

      await expect(patientService.delete('1')).rejects.toThrow('Delete failed');
    });
  });
});

