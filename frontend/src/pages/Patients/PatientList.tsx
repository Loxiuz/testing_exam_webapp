import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { patientService } from '../../services/patientService';
import { Patient } from '../../types';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { ErrorMessage } from '../../components/common/ErrorMessage';
import { useAuth } from '../../context/AuthContext';
import { format } from 'date-fns';

export const PatientList: React.FC = () => {
  const [patients, setPatients] = useState<Patient[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { role } = useAuth();
  const navigate = useNavigate();
  const isAdmin = role === 'ADMIN';

  useEffect(() => {
    loadPatients();
  }, []);

  const loadPatients = async () => {
    try {
      setLoading(true);
      const data = await patientService.getAll();
      setPatients(data);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load patients');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: string) => {
    if (!window.confirm('Are you sure you want to delete this patient?')) {
      return;
    }

    try {
      await patientService.delete(id);
      loadPatients();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to delete patient');
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
              <h1 className="text-3xl font-bold text-gray-900">Patients</h1>
              <p className="mt-1 text-sm text-gray-500">Manage patient records</p>
            </div>
            {isAdmin && (
              <button
                onClick={() => navigate('/patients/new')}
                className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-md shadow-sm"
              >
                Add New Patient
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Content Section */}
      <div className="bg-white shadow rounded-lg">
        <div className="px-4 py-5 sm:px-6">
          {error && <ErrorMessage message={error} onDismiss={() => setError(null)} />}

          {patients.length === 0 ? (
            <div className="text-center py-12">
              <p className="text-gray-500 text-lg">No patients found.</p>
              {isAdmin && (
                <button
                  onClick={() => navigate('/patients/new')}
                  className="mt-4 text-blue-600 hover:text-blue-800"
                >
                  Create your first patient
                </button>
              )}
            </div>
          ) : (
            <div className="overflow-hidden">
              <ul className="divide-y divide-gray-200">
                {patients.map((patient) => (
                  <li key={patient.patientId}>
                    <div className="px-4 py-4 sm:px-6 hover:bg-gray-50">
                      <div className="flex items-center justify-between">
                        <div className="flex items-center">
                          <div>
                            <Link
                              to={`/patients/${patient.patientId}`}
                              className="text-lg font-medium text-blue-600 hover:text-blue-800"
                            >
                              {patient.patientName}
                            </Link>
                            <div className="mt-2 sm:flex sm:justify-between">
                              <div className="sm:flex">
                                <p className="flex items-center text-sm text-gray-500">
                                  DOB: {format(new Date(patient.dateOfBirth), 'MMM dd, yyyy')}
                                </p>
                                {patient.gender && (
                                  <p className="mt-2 flex items-center text-sm text-gray-500 sm:mt-0 sm:ml-6">
                                    Gender: {patient.gender}
                                  </p>
                                )}
                              </div>
                            </div>
                          </div>
                        </div>
                        <div className="flex space-x-2">
                          <Link
                            to={`/patients/${patient.patientId}`}
                            className="text-blue-600 hover:text-blue-800 text-sm font-medium"
                          >
                            View
                          </Link>
                          {isAdmin && (
                            <>
                              <Link
                                to={`/patients/${patient.patientId}/edit`}
                                className="text-green-600 hover:text-green-800 text-sm font-medium"
                              >
                                Edit
                              </Link>
                              <button
                                onClick={() => handleDelete(patient.patientId)}
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

