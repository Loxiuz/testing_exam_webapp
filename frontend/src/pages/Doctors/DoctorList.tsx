import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { doctorService } from '../../services/doctorService';
import { Doctor } from '../../types';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { ErrorMessage } from '../../components/common/ErrorMessage';
import { useAuth } from '../../context/AuthContext';

export const DoctorList: React.FC = () => {
  const [doctors, setDoctors] = useState<Doctor[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { role } = useAuth();
  const navigate = useNavigate();
  const isAdmin = role === 'ADMIN';

  useEffect(() => {
    loadDoctors();
  }, []);

  const loadDoctors = async () => {
    try {
      setLoading(true);
      const data = await doctorService.getAll();
      setDoctors(data);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load doctors');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: string) => {
    if (!window.confirm('Are you sure you want to delete this doctor?')) {
      return;
    }

    try {
      await doctorService.delete(id);
      loadDoctors();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to delete doctor');
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <LoadingSpinner size="large" />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header Section */}
      <div className="bg-white shadow rounded-lg">
        <div className="px-4 py-5 sm:px-6">
          <div className="flex justify-between items-center">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">Doctors</h1>
              <p className="mt-1 text-sm text-gray-500">Manage doctor records</p>
            </div>
            {isAdmin && (
              <button
                onClick={() => navigate('/doctors/new')}
                className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-md shadow-sm"
              >
                Add New Doctor
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Content Section */}
      <div className="bg-white shadow rounded-lg">
        <div className="px-4 py-5 sm:px-6">
          {error && <ErrorMessage message={error} onDismiss={() => setError(null)} />}

      {doctors.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-gray-500 text-lg">No doctors found.</p>
          {isAdmin && (
            <button
              onClick={() => navigate('/doctors/new')}
              className="mt-4 text-blue-600 hover:text-blue-800"
            >
              Create your first doctor
            </button>
          )}
        </div>
      ) : (
        <div className="bg-white shadow overflow-hidden sm:rounded-md">
          <ul className="divide-y divide-gray-200">
            {doctors.map((doctor) => (
              <li key={doctor.doctorId}>
                <div className="px-4 py-4 sm:px-6 hover:bg-gray-50">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center">
                      <div>
                        <Link
                          to={`/doctors/${doctor.doctorId}`}
                          className="text-lg font-medium text-blue-600 hover:text-blue-800"
                        >
                          {doctor.doctorName}
                        </Link>
                        <div className="mt-2 flex flex-wrap items-center gap-x-4 gap-y-1">
                          <span className="text-sm text-gray-500">
                            <span className="font-medium">Speciality:</span> {doctor.speciality}
                          </span>
                          {doctor.hospital && (
                            <span className="text-sm text-gray-500">
                              <span className="font-medium">Hospital:</span> {doctor.hospital.hospitalName} - {doctor.hospital.city}
                            </span>
                          )}
                          {doctor.ward && (
                            <span className="text-sm text-gray-500">
                              <span className="font-medium">Ward:</span> {doctor.ward.type} Ward
                            </span>
                          )}
                        </div>
                      </div>
                    </div>
                    <div className="flex space-x-2">
                      <Link
                        to={`/doctors/${doctor.doctorId}`}
                        className="text-blue-600 hover:text-blue-800 text-sm font-medium"
                      >
                        View
                      </Link>
                      {isAdmin && (
                        <>
                          <Link
                            to={`/doctors/${doctor.doctorId}/edit`}
                            className="text-green-600 hover:text-green-800 text-sm font-medium"
                          >
                            Edit
                          </Link>
                          <button
                            onClick={() => handleDelete(doctor.doctorId)}
                            className="text-red-600 hover:text-red-800 text-sm font-medium"
                          >
                            Delete
                          </button>
                        </>
                      )}
                    </div>
                  </div>
                </div>
              </li>
            ))}
          </ul>
        </div>
        )}
        </div>
      </div>
    </div>
  );
};

