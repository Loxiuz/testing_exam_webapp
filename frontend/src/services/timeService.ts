import api from './api';
import { Time } from '../types';

export const timeService = {
  getCurrentTime: async (timezone?: string): Promise<Time> => {
    const url = timezone 
      ? `/api/time?timezone=${encodeURIComponent(timezone)}` 
      : '/api/time';
    const response = await api.get<Time>(url);
    return response.data;
  },
};

