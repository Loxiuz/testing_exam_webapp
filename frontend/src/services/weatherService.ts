import api from './api';
import { Weather } from '../types';

export const weatherService = {
  getWeatherByCity: async (city?: string): Promise<Weather> => {
    const url = city ? `/api/weather?city=${encodeURIComponent(city)}` : '/api/weather';
    const response = await api.get<Weather>(url);
    return response.data;
  },
};

