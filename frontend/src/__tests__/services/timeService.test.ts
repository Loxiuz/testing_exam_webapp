import { describe, it, expect, vi, beforeEach } from 'vitest';
import { timeService } from '../../services/timeService';
import api from '../../services/api';
import { Time } from '../../types';

vi.mock('../../services/api', () => ({
  default: {
    get: vi.fn(),
  },
}));

describe('timeService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('getCurrentTime', () => {
    it('should fetch current time for a specific timezone', async () => {
      const mockTime: Time = {
        datetime: '2024-12-12T12:00:00',
        timezone: 'America/New_York',
        abbreviation: 'EST',
        dayOfWeek: 4,
        dayOfYear: 347,
      };

      (api.get as any).mockResolvedValue({ data: mockTime });

      const result = await timeService.getCurrentTime('America/New_York');

      expect(result).toEqual(mockTime);
      expect(api.get).toHaveBeenCalledWith('/api/time?timezone=America%2FNew_York');
    });

    it('should fetch default time when no timezone is provided', async () => {
      const mockTime: Time = {
        datetime: '2024-12-12T18:00:00',
        timezone: 'Europe/Copenhagen',
        abbreviation: 'CET',
        dayOfWeek: 4,
        dayOfYear: 347,
      };

      (api.get as any).mockResolvedValue({ data: mockTime });

      const result = await timeService.getCurrentTime();

      expect(result).toEqual(mockTime);
      expect(api.get).toHaveBeenCalledWith('/api/time');
    });

    it('should handle errors when fetching time', async () => {
      const error = new Error('Network error');
      (api.get as any).mockRejectedValue(error);

      await expect(timeService.getCurrentTime()).rejects.toThrow('Network error');
    });
  });
});

