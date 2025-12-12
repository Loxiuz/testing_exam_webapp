import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { WeatherWidget } from '../../../components/widgets/WeatherWidget';
import { weatherService } from '../../../services/weatherService';

vi.mock('../../../services/weatherService');

describe('WeatherWidget', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should render weather widget with data', async () => {
    const mockWeather = {
      city: 'Copenhagen',
      country: 'DK',
      temperature: 15.5,
      description: 'Partly cloudy',
      icon: '02d',
      humidity: 65.0,
      windSpeed: 10.0,
      condition: 'Clouds',
    };

    (weatherService.getWeatherByCity as any).mockResolvedValue(mockWeather);

    render(<WeatherWidget />);

    await waitFor(() => {
      expect(screen.getByText('Weather')).toBeInTheDocument();
      expect(screen.getByText('Copenhagen, DK')).toBeInTheDocument();
      expect(screen.getByText('16°C')).toBeInTheDocument();
    });
  });

  it('should render weather widget for specific city', async () => {
    const mockWeather = {
      city: 'London',
      country: 'GB',
      temperature: 12.0,
      description: 'Rain',
      icon: '10d',
      humidity: 80.0,
      windSpeed: 15.0,
      condition: 'Rain',
    };

    (weatherService.getWeatherByCity as any).mockResolvedValue(mockWeather);

    render(<WeatherWidget city="London" />);

    await waitFor(() => {
      expect(weatherService.getWeatherByCity).toHaveBeenCalledWith('London');
      expect(screen.getByText('London, GB')).toBeInTheDocument();
    });
  });

  it('should display loading state initially', () => {
    (weatherService.getWeatherByCity as any).mockImplementation(
      () => new Promise(() => {}) // Never resolves
    );

    render(<WeatherWidget />);

    expect(document.querySelector('.animate-spin')).toBeInTheDocument();
  });

  it('should display error message on failure', async () => {
    const error = new Error('Failed to load weather');
    (weatherService.getWeatherByCity as any).mockRejectedValue(error);

    render(<WeatherWidget />);

    await waitFor(() => {
      expect(screen.getByText(/Failed to load weather/i)).toBeInTheDocument();
    });
  });
});

