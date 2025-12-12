import { describe, it, expect, vi, beforeEach } from 'vitest';
import { doctorService } from '../../services/doctorService';
import api from '../../services/api';

vi.mock('../../services/api', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

describe('doctorService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should fetch all doctors successfully', async () => {
    const mockDoctors = [
      {
        doctorId: '1',
        doctorName: 'Dr. Smith',
        speciality: 'CARDIOLOGY',
      },
    ];

    (api.get as any).mockResolvedValue({ data: mockDoctors });

    const result = await doctorService.getAll();

    expect(result).toEqual(mockDoctors);
    expect(api.get).toHaveBeenCalledWith('/doctors/all');
  });

  it('should create doctor successfully', async () => {
    const request = {
      doctorName: 'Dr. Jones',
      speciality: 'NEUROLOGY',
    };

    const mockDoctor = {
      doctorId: '2',
      ...request,
    };

    (api.post as any).mockResolvedValue({ data: mockDoctor });

    const result = await doctorService.create(request);

    expect(result).toEqual(mockDoctor);
    expect(api.post).toHaveBeenCalledWith('/doctors/create', request);
  });

  it('should handle network errors', async () => {
    const error = new Error('Network error');
    (api.get as any).mockRejectedValue(error);

    await expect(doctorService.getAll()).rejects.toThrow('Network error');
  });
});

