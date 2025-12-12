import api from './api';
import { Nurse, NurseRequest } from '../types';

export const nurseService = {
  getAll: async (): Promise<Nurse[]> => {
    const response = await api.get<Nurse[]>('/nurses/all');
    return response.data;
  },

  getById: async (id: string): Promise<Nurse> => {
    const response = await api.get<Nurse>(`/nurses/${id}`);
    return response.data;
  },

  create: async (data: NurseRequest): Promise<Nurse> => {
    const response = await api.post<Nurse>('/nurses/create', data);
    return response.data;
  },

  update: async (id: string, data: NurseRequest): Promise<Nurse> => {
    const response = await api.put<Nurse>(`/nurses/update/${id}`, data);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await api.delete(`/nurses/delete/${id}`);
  }
};

