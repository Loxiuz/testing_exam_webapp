import api from './api';
import { Ward, WardRequest } from '../types';

export const wardService = {
  getAll: async (): Promise<Ward[]> => {
    try {
      const response = await api.get<Ward[]>('/wards/all');
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

  getByHospitalId: async (hospitalId: string): Promise<Ward[]> => {
    try {
      const response = await api.get<Ward[]>(`/wards/by-hospital/${hospitalId}`);
      if (response.status === 204 || !response.data) {
        return [];
      }
      return response.data;
    } catch (error: any) {
      if (error.response?.status === 204) {
        return [];
      }
      throw error;
    }
  },

  getById: async (id: string): Promise<Ward> => {
    const response = await api.get<Ward>(`/wards/${id}`);
    return response.data;
  },

  create: async (data: WardRequest): Promise<Ward> => {
    const response = await api.post<Ward>('/wards/create', data);
    return response.data;
  },

  update: async (id: string, data: WardRequest): Promise<Ward> => {
    const response = await api.put<Ward>(`/wards/update/${id}`, data);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await api.delete(`/wards/delete/${id}`);
  }
};
