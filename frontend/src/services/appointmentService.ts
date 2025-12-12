import api from './api';
import { Appointment, AppointmentRequest } from '../types';

export const appointmentService = {
  getAll: async (): Promise<Appointment[]> => {
    const response = await api.get<Appointment[]>('/appointments/all');
    return response.data;
  },

  getById: async (id: string): Promise<Appointment> => {
    const response = await api.get<Appointment>(`/appointments/${id}`);
    return response.data;
  },

  create: async (data: AppointmentRequest): Promise<Appointment> => {
    const response = await api.post<Appointment>('/appointments/create', data);
    return response.data;
  },

  update: async (id: string, data: AppointmentRequest): Promise<Appointment> => {
    const response = await api.put<Appointment>(`/appointments/update/${id}`, data);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await api.delete(`/appointments/delete/${id}`);
  }
};

