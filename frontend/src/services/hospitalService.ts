import api from './api';
import { Hospital, HospitalRequest } from '../types';

export const hospitalService = {
  getAll: async (): Promise<Hospital[]> => {
    try {
      const response = await api.get<Hospital[]>('/hospitals/all');
      // Handle 204 NO_CONTENT response
      if (response.status === 204 || !response.data) {
        return [];
      }
      return response.data;
    } catch (error: any) {
      // If it's a 204, return empty array instead of throwing
      if (error.response?.status === 204) {
        return [];
      }
      throw error;
    }
  },

  getById: async (id: string): Promise<Hospital> => {
    const response = await api.get<Hospital>(`/hospitals/${id}`);
    return response.data;
  },

  create: async (data: HospitalRequest): Promise<Hospital> => {
    const response = await api.post<Hospital>('/hospitals/create', data);
    return response.data;
  },

  update: async (id: string, data: HospitalRequest): Promise<Hospital> => {
    const response = await api.put<Hospital>(`/hospitals/update/${id}`, data);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await api.delete(`/hospitals/delete/${id}`);
  }
};
