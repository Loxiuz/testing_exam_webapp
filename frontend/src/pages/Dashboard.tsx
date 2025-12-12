import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { patientService } from '../services/patientService';
import { doctorService } from '../services/doctorService';
import { nurseService } from '../services/nurseService';
import { appointmentService } from '../services/appointmentService';
import { hospitalService } from '../services/hospitalService';
import { wardService } from '../services/wardService';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { useAuth } from '../context/AuthContext';
import { WeatherWidget } from '../components/widgets/WeatherWidget';
import { ClockWidget } from '../components/widgets/ClockWidget';

export const Dashboard: React.FC = () => {
  const [stats, setStats] = useState({
    patients: 0,
    doctors: 0,
    nurses: 0,
    appointments: 0,
    hospitals: 0,
    wards: 0,
    loading: true
  });
  const { role } = useAuth();
  const isAdmin = role === 'ADMIN';

  useEffect(() => {
    loadStats();
  }, []);

  const loadStats = async () => {
    try {
      const results = await Promise.allSettled([
        patientService.getAll(),
        doctorService.getAll(),
        nurseService.getAll(),
        appointmentService.getAll(),
        hospitalService.getAll(),
        wardService.getAll()
      ]);
      
      const [patients, doctors, nurses, appointments, hospitals, wards] = results.map((result, index) => {
        if (result.status === 'fulfilled') {
          return result.value;
        } else {
          console.error(`Error loading stat ${index}:`, result.reason);
          return [];
        }
      });
      
      setStats({
        patients: Array.isArray(patients) ? patients.length : 0,
        doctors: Array.isArray(doctors) ? doctors.length : 0,
        nurses: Array.isArray(nurses) ? nurses.length : 0,
        appointments: Array.isArray(appointments) ? appointments.length : 0,
        hospitals: Array.isArray(hospitals) ? hospitals.length : 0,
        wards: Array.isArray(wards) ? wards.length : 0,
        loading: false
      });
    } catch (error) {
      console.error('Error loading dashboard stats:', error);
      setStats(prev => ({ ...prev, loading: false }));
    }
  };

  if (stats.loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <LoadingSpinner size="large" />
      </div>
    );
  }

  return (
    <div>
      <h1 className="text-3xl font-bold text-gray-900 mb-6">Dashboard</h1>

      {/* Widgets Section */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
        <WeatherWidget />
        <ClockWidget />
      </div>

      {/* View Section */}
      <div className="bg-white shadow rounded-lg">
        <div className="px-4 py-5 sm:p-6">
          <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">View Records</h3>
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
            <Link
              to="/patients"
              className="relative group bg-white p-6 focus-within:ring-2 focus-within:ring-inset focus-within:ring-blue-500 rounded-lg border border-gray-200 hover:border-blue-500"
            >
              <div className="flex items-center justify-between">
                <div>
                  <span className="rounded-lg inline-flex p-3 bg-blue-50 text-blue-700">
                    <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                    </svg>
                  </span>
                </div>
                <div className="text-2xl font-bold text-gray-900">{stats.patients}</div>
              </div>
              <div className="mt-4">
                <h3 className="text-lg font-medium">
                  <span className="absolute inset-0" aria-hidden="true" />
                  View Patients
                </h3>
                <p className="mt-2 text-sm text-gray-500">Browse and manage patient records</p>
              </div>
            </Link>

            <Link
              to="/doctors"
              className="relative group bg-white p-6 focus-within:ring-2 focus-within:ring-inset focus-within:ring-blue-500 rounded-lg border border-gray-200 hover:border-blue-500"
            >
              <div className="flex items-center justify-between">
                <div>
                  <span className="rounded-lg inline-flex p-3 bg-green-50 text-green-700">
                    <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                  </span>
                </div>
                <div className="text-2xl font-bold text-gray-900">{stats.doctors}</div>
              </div>
              <div className="mt-4">
                <h3 className="text-lg font-medium">
                  <span className="absolute inset-0" aria-hidden="true" />
                  View Doctors
                </h3>
                <p className="mt-2 text-sm text-gray-500">Browse and manage doctor records</p>
              </div>
            </Link>

            <Link
              to="/nurses"
              className="relative group bg-white p-6 focus-within:ring-2 focus-within:ring-inset focus-within:ring-blue-500 rounded-lg border border-gray-200 hover:border-blue-500"
            >
              <div className="flex items-center justify-between">
                <div>
                  <span className="rounded-lg inline-flex p-3 bg-pink-50 text-pink-700">
                    <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                    </svg>
                  </span>
                </div>
                <div className="text-2xl font-bold text-gray-900">{stats.nurses}</div>
              </div>
              <div className="mt-4">
                <h3 className="text-lg font-medium">
                  <span className="absolute inset-0" aria-hidden="true" />
                  View Nurses
                </h3>
                <p className="mt-2 text-sm text-gray-500">Browse and manage nurse records</p>
              </div>
            </Link>

            <Link
              to="/appointments"
              className="relative group bg-white p-6 focus-within:ring-2 focus-within:ring-inset focus-within:ring-blue-500 rounded-lg border border-gray-200 hover:border-blue-500"
            >
              <div className="flex items-center justify-between">
                <div>
                  <span className="rounded-lg inline-flex p-3 bg-purple-50 text-purple-700">
                    <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                  </span>
                </div>
                <div className="text-2xl font-bold text-gray-900">{stats.appointments}</div>
              </div>
              <div className="mt-4">
                <h3 className="text-lg font-medium">
                  <span className="absolute inset-0" aria-hidden="true" />
                  View Appointments
                </h3>
                <p className="mt-2 text-sm text-gray-500">Browse and manage appointments</p>
              </div>
            </Link>

            <Link
              to="/hospitals"
              className="relative group bg-white p-6 focus-within:ring-2 focus-within:ring-inset focus-within:ring-blue-500 rounded-lg border border-gray-200 hover:border-blue-500"
            >
              <div className="flex items-center justify-between">
                <div>
                  <span className="rounded-lg inline-flex p-3 bg-indigo-50 text-indigo-700">
                    <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                    </svg>
                  </span>
                </div>
                <div className="text-2xl font-bold text-gray-900">{stats.hospitals ?? 0}</div>
              </div>
              <div className="mt-4">
                <h3 className="text-lg font-medium">
                  <span className="absolute inset-0" aria-hidden="true" />
                  View Hospitals
                </h3>
                <p className="mt-2 text-sm text-gray-500">Browse and manage hospitals</p>
              </div>
            </Link>

            <Link
              to="/wards"
              className="relative group bg-white p-6 focus-within:ring-2 focus-within:ring-inset focus-within:ring-blue-500 rounded-lg border border-gray-200 hover:border-blue-500"
            >
              <div className="flex items-center justify-between">
                <div>
                  <span className="rounded-lg inline-flex p-3 bg-teal-50 text-teal-700">
                    <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
                    </svg>
                  </span>
                </div>
                <div className="text-2xl font-bold text-gray-900">{stats.wards ?? 0}</div>
              </div>
              <div className="mt-4">
                <h3 className="text-lg font-medium">
                  <span className="absolute inset-0" aria-hidden="true" />
                  View Wards
                </h3>
                <p className="mt-2 text-sm text-gray-500">Browse and manage wards</p>
              </div>
            </Link>
          </div>
        </div>
      </div>

      {/* Add/Create Section */}
      <div className="bg-white shadow rounded-lg">
        <div className="px-4 py-5 sm:p-6">
          <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">Create New Records</h3>
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {isAdmin && (
              <>
                <Link
                  to="/patients/new"
                  className="relative group bg-white p-6 focus-within:ring-2 focus-within:ring-inset focus-within:ring-blue-500 rounded-lg border border-gray-200 hover:border-blue-500"
                >
                  <div>
                    <span className="rounded-lg inline-flex p-3 bg-blue-50 text-blue-700">
                      <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                      </svg>
                    </span>
                  </div>
                  <div className="mt-4">
                    <h3 className="text-lg font-medium">
                      <span className="absolute inset-0" aria-hidden="true" />
                      Add Patient
                    </h3>
                    <p className="mt-2 text-sm text-gray-500">Create a new patient record</p>
                  </div>
                </Link>

                <Link
                  to="/doctors/new"
                  className="relative group bg-white p-6 focus-within:ring-2 focus-within:ring-inset focus-within:ring-blue-500 rounded-lg border border-gray-200 hover:border-blue-500"
                >
                  <div>
                    <span className="rounded-lg inline-flex p-3 bg-green-50 text-green-700">
                      <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                      </svg>
                    </span>
                  </div>
                  <div className="mt-4">
                    <h3 className="text-lg font-medium">
                      <span className="absolute inset-0" aria-hidden="true" />
                      Add Doctor
                    </h3>
                    <p className="mt-2 text-sm text-gray-500">Create a new doctor record</p>
                  </div>
                </Link>

                <Link
                  to="/nurses/new"
                  className="relative group bg-white p-6 focus-within:ring-2 focus-within:ring-inset focus-within:ring-blue-500 rounded-lg border border-gray-200 hover:border-blue-500"
                >
                  <div>
                    <span className="rounded-lg inline-flex p-3 bg-pink-50 text-pink-700">
                      <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                      </svg>
                    </span>
                  </div>
                  <div className="mt-4">
                    <h3 className="text-lg font-medium">
                      <span className="absolute inset-0" aria-hidden="true" />
                      Add Nurse
                    </h3>
                    <p className="mt-2 text-sm text-gray-500">Create a new nurse record</p>
                  </div>
                </Link>

                <Link
                  to="/hospitals/new"
                  className="relative group bg-white p-6 focus-within:ring-2 focus-within:ring-inset focus-within:ring-blue-500 rounded-lg border border-gray-200 hover:border-blue-500"
                >
                  <div>
                    <span className="rounded-lg inline-flex p-3 bg-indigo-50 text-indigo-700">
                      <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                      </svg>
                    </span>
                  </div>
                  <div className="mt-4">
                    <h3 className="text-lg font-medium">
                      <span className="absolute inset-0" aria-hidden="true" />
                      Add Hospital
                    </h3>
                    <p className="mt-2 text-sm text-gray-500">Create a new hospital</p>
                  </div>
                </Link>

                <Link
                  to="/wards/new"
                  className="relative group bg-white p-6 focus-within:ring-2 focus-within:ring-inset focus-within:ring-blue-500 rounded-lg border border-gray-200 hover:border-blue-500"
                >
                  <div>
                    <span className="rounded-lg inline-flex p-3 bg-teal-50 text-teal-700">
                      <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                      </svg>
                    </span>
                  </div>
                  <div className="mt-4">
                    <h3 className="text-lg font-medium">
                      <span className="absolute inset-0" aria-hidden="true" />
                      Add Ward
                    </h3>
                    <p className="mt-2 text-sm text-gray-500">Create a new ward</p>
                  </div>
                </Link>
              </>
            )}

            <Link
              to="/appointments/new"
              className="relative group bg-white p-6 focus-within:ring-2 focus-within:ring-inset focus-within:ring-blue-500 rounded-lg border border-gray-200 hover:border-blue-500"
            >
              <div>
                <span className="rounded-lg inline-flex p-3 bg-purple-50 text-purple-700">
                  <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                  </svg>
                </span>
              </div>
              <div className="mt-4">
                <h3 className="text-lg font-medium">
                  <span className="absolute inset-0" aria-hidden="true" />
                  Schedule Appointment
                </h3>
                <p className="mt-2 text-sm text-gray-500">Create a new appointment</p>
              </div>
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

