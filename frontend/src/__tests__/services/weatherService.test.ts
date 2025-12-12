import { describe, it, expect, vi, beforeEach } from 'vitest';
import { weatherService } from '../../services/weatherService';
import api from '../../services/api';
import { Weather } from '../../types';

vi.mock('../../services/api', () => ({
  default: {
    get: vi.fn(),
  },
}));

describe('weatherService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('getWeatherByCity', () => {
    it('should fetch weather for a specific city', async () => {
      const mockWeather: Weather = {
        city: 'Copenhagen',
        country: 'DK',
        temperature: 15.5,
        description: 'Partly cloudy',
        icon: '02d',
        humidity: 65.0,
        windSpeed: 10.0,
        condition: 'Clouds',
      };

      (api.get as any).mockResolvedValue({ data: mockWeather });

      const result = await weatherService.getWeatherByCity('Copenhagen');

      expect(result).toEqual(mockWeather);
      expect(api.get).toHaveBeenCalledWith('/api/weather?city=Copenhagen');
    });

    it('should fetch default weather when no city is provided', async () => {
      const mockWeather: Weather = {
        city: 'Copenhagen',
        country: 'DK',
        temperature: 15.0,
        description: 'Clear sky',
        icon: '01d',
        humidity: 60.0,
        windSpeed: 8.0,
        condition: 'Clear',
      };

      (api.get as any).mockResolvedValue({ data: mockWeather });

      const result = await weatherService.getWeatherByCity();

      expect(result).toEqual(mockWeather);
      expect(api.get).toHaveBeenCalledWith('/api/weather');
    });

    it('should handle errors when fetching weather', async () => {
      const error = new Error('Network error');
      (api.get as any).mockRejectedValue(error);

      await expect(weatherService.getWeatherByCity('Copenhagen')).rejects.toThrow('Network error');
    });
  });
});

