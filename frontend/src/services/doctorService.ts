import api from './api';
import { Doctor, DoctorRequest } from '../types';

export const doctorService = {
  getAll: async (): Promise<Doctor[]> => {
    const response = await api.get<Doctor[]>('/doctors/all');
    return response.data;
  },

  getById: async (id: string): Promise<Doctor> => {
    const response = await api.get<Doctor>(`/doctors/${id}`);
    return response.data;
  },

  create: async (data: DoctorRequest): Promise<Doctor> => {
    const response = await api.post<Doctor>('/doctors/create', data);
    return response.data;
  },

  update: async (id: string, data: DoctorRequest): Promise<Doctor> => {
    const response = await api.put<Doctor>(`/doctors/update/${id}`, data);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await api.delete(`/doctors/delete/${id}`);
  }
};

