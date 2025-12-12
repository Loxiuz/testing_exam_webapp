import api from './api';
import { Patient, PatientRequest } from '../types';

export const patientService = {
  getAll: async (): Promise<Patient[]> => {
    const response = await api.get<Patient[]>('/patients/all');
    return response.data;
  },

  getById: async (id: string): Promise<Patient> => {
    const response = await api.get<Patient>(`/patients/${id}`);
    return response.data;
  },

  create: async (data: PatientRequest): Promise<Patient> => {
    const response = await api.post<Patient>('/patients/create', data);
    return response.data;
  },

  update: async (id: string, data: PatientRequest): Promise<Patient> => {
    const response = await api.put<Patient>(`/patients/update/${id}`, data);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await api.delete(`/patients/delete/${id}`);
  }
};

