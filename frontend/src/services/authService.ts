import api from './api';
import { LoginRequest, LoginResponse, RegisterRequest, User } from '../types';
import { tokenStorage } from '../utils/tokenStorage';

export const authService = {
  login: async (credentials: LoginRequest): Promise<LoginResponse> => {
    const response = await api.post<LoginResponse>('/auth/login', credentials);
    const { token, role } = response.data;
    tokenStorage.setToken(token);
    tokenStorage.setRole(role);
    return response.data;
  },

  register: async (data: RegisterRequest): Promise<User> => {
    const response = await api.post<User>('/auth/register', data);
    return response.data;
  },

  logout: (): void => {
    tokenStorage.removeToken();
  },

  isAuthenticated: (): boolean => {
    return !!tokenStorage.getToken();
  },

  getRole: (): string | null => {
    return tokenStorage.getRole();
  }
};

