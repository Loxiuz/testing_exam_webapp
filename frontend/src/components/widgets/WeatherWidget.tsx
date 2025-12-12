import React, { useEffect, useState } from 'react';
import { weatherService } from '../../services/weatherService';
import { Weather } from '../../types';
import { LoadingSpinner } from '../common/LoadingSpinner';
import { ErrorMessage } from '../common/ErrorMessage';

interface WeatherWidgetProps {
  city?: string;
  className?: string;
}

export const WeatherWidget: React.FC<WeatherWidgetProps> = ({ city, className = '' }) => {
  const [weather, setWeather] = useState<Weather | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadWeather();
  }, [city]);

  const loadWeather = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await weatherService.getWeatherByCity(city);
      setWeather(data);
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to load weather');
    } finally {
      setLoading(false);
    }
  };

  const getWeatherIcon = (icon: string) => {
    return `https://openweathermap.org/img/wn/${icon}@2x.png`;
  };

  if (loading) {
    return (
      <div className={`bg-white shadow rounded-lg p-6 ${className}`}>
        <div className="flex justify-center items-center h-32">
          <LoadingSpinner size="small" />
        </div>
      </div>
    );
  }

  if (error || !weather) {
    return (
      <div className={`bg-white shadow rounded-lg p-6 ${className}`}>
        <ErrorMessage message={error || 'Weather data unavailable'} />
      </div>
    );
  }

  return (
    <div className={`bg-gradient-to-br from-blue-500 to-blue-600 text-white shadow-lg rounded-lg p-6 ${className}`}>
      <div className="flex items-center justify-between mb-4">
        <div>
          <h3 className="text-lg font-semibold">Weather</h3>
          <p className="text-sm text-blue-100">
            {weather.city}, {weather.country}
          </p>
        </div>
        {weather.icon && (
          <img
            src={getWeatherIcon(weather.icon)}
            alt={weather.description}
            className="w-16 h-16"
          />
        )}
      </div>
      
      <div className="mt-4">
        <div className="flex items-baseline">
          <span className="text-4xl font-bold">{Math.round(weather.temperature)}</span>
          <span className="text-xl ml-1">°C</span>
        </div>
        <p className="text-blue-100 capitalize mt-1">{weather.description}</p>
      </div>

      <div className="mt-4 pt-4 border-t border-blue-400 border-opacity-30 grid grid-cols-2 gap-4 text-sm">
        <div>
          <p className="text-blue-200">Humidity</p>
          <p className="font-semibold">{Math.round(weather.humidity)}%</p>
        </div>
        <div>
          <p className="text-blue-200">Wind Speed</p>
          <p className="font-semibold">{Math.round(weather.windSpeed)} m/s</p>
        </div>
      </div>
    </div>
  );
};

