export enum Role {
  USER = 'USER',
  ADMIN = 'ADMIN'
}

export enum DoctorSpecialityType {
  CARDIOLOGY = 'CARDIOLOGY',
  NEUROLOGY = 'NEUROLOGY',
  GENERAL_MEDICINE = 'GENERAL_MEDICINE',
  SURGERY = 'SURGERY'
}

export enum AppointmentStatusType {
  SCHEDULED = 'SCHEDULED',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED'
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  role: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  role?: Role;
}

export interface Patient {
  patientId: string;
  patientName: string;
  dateOfBirth: string; // ISO date string
  gender?: string;
  ward?: Ward;
  hospital?: Hospital;
}

export interface PatientRequest {
  patientName: string;
  dateOfBirth: string; // ISO date string (YYYY-MM-DD)
  gender?: string;
  wardId?: string;
  hospitalId?: string;
}

export interface Doctor {
  doctorId: string;
  doctorName: string;
  speciality: DoctorSpecialityType;
  ward?: Ward;
  hospital?: Hospital;
}

export interface DoctorRequest {
  doctorName: string;
  speciality: DoctorSpecialityType;
  wardId?: string;
  hospitalId?: string;
}

export interface Appointment {
  appointmentId: string;
  appointmentDate: string; // ISO date string
  reason?: string;
  status: AppointmentStatusType;
  patient?: Patient;
  doctor?: Doctor;
  nurse?: Nurse;
}

export interface AppointmentRequest {
  appointmentDate: string; // ISO date string (YYYY-MM-DD)
  reason?: string;
  status: AppointmentStatusType;
  patientId?: string;
  doctorId?: string;
  nurseId?: string;
}

export interface Ward {
  wardId: string;
  type: WardType;
  maxCapacity: number;
  hospitals?: Hospital[];
}

export interface WardRequest {
  type: WardType;
  maxCapacity: number;
  hospitalIds?: string[];
}

export enum WardType {
  CARDIOLOGY = 'CARDIOLOGY',
  NEUROLOGY = 'NEUROLOGY',
  GENERAL_MEDICINE = 'GENERAL_MEDICINE'
}

export interface Hospital {
  hospitalId: string;
  hospitalName: string;
  address: string;
  city: string;
  wards?: Ward[];
}

export interface HospitalRequest {
  hospitalName: string;
  address: string;
  city: string;
}

export enum NurseSpecialityType {
  EMERGENCY = 'EMERGENCY',
  ICU = 'ICU',
  GENERAL_CARE = 'GENERAL_CARE'
}

export interface Nurse {
  nurseId: string;
  nurseName: string;
  speciality: NurseSpecialityType;
  ward?: Ward;
  hospital?: Hospital;
}

export interface NurseRequest {
  nurseName: string;
  speciality: NurseSpecialityType;
  wardId?: string;
  hospitalId?: string;
}

export interface User {
  userId: string;
  username: string;
  role: Role;
}

// Weather Types
export interface Weather {
  city: string;
  country: string;
  temperature: number;
  description: string;
  icon: string;
  humidity: number;
  windSpeed: number;
  condition: string;
}

// Time Types
export interface Time {
  datetime: string;
  timezone: string;
  abbreviation: string;
  dayOfWeek: number;
  dayOfYear: number;
}

