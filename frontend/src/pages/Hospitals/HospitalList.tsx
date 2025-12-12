import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { hospitalService } from '../../services/hospitalService';
import { Hospital } from '../../types';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { ErrorMessage } from '../../components/common/ErrorMessage';
import { useAuth } from '../../context/AuthContext';

export const HospitalList: React.FC = () => {
  const [hospitals, setHospitals] = useState<Hospital[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { role } = useAuth();
  const navigate = useNavigate();
  const isAdmin = role === 'ADMIN';

  useEffect(() => {
    loadHospitals();
  }, []);

  const loadHospitals = async () => {
    try {
      setLoading(true);
      const data = await hospitalService.getAll();
      console.log('Loaded hospitals:', data); // Debug log
      setHospitals(data || []);
      setError(null);
    } catch (err: any) {
      console.error('Error loading hospitals:', err); // Debug log
      setError(err.response?.data?.message || err.message || 'Failed to load hospitals');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: string) => {
    if (!window.confirm('Are you sure you want to delete this hospital?')) {
      return;
    }

    try {
      await hospitalService.delete(id);
      loadHospitals();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to delete hospital');
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
              <h1 className="text-3xl font-bold text-gray-900">Hospitals</h1>
              <p className="mt-1 text-sm text-gray-500">Manage hospital records</p>
            </div>
            {isAdmin && (
              <button
                onClick={() => navigate('/hospitals/new')}
                className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-md shadow-sm"
              >
                Add New Hospital
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Content Section */}
      <div className="bg-white shadow rounded-lg">
        <div className="px-4 py-5 sm:px-6">
          {error && <ErrorMessage message={error} onDismiss={() => setError(null)} />}

      {hospitals.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-gray-500 text-lg">No hospitals found.</p>
          {isAdmin && (
            <button
              onClick={() => navigate('/hospitals/new')}
              className="mt-4 text-blue-600 hover:text-blue-800"
            >
              Create your first hospital
            </button>
          )}
        </div>
      ) : (
        <div className="bg-white shadow overflow-hidden sm:rounded-md">
          <ul className="divide-y divide-gray-200">
            {hospitals.map((hospital) => (
              <li key={hospital.hospitalId}>
                <div className="px-4 py-4 sm:px-6 hover:bg-gray-50">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center">
                      <div>
                        <Link
                          to={`/hospitals/${hospital.hospitalId}`}
                          className="text-lg font-medium text-blue-600 hover:text-blue-800"
                        >
                          {hospital.hospitalName}
                        </Link>
                        <div className="mt-2 sm:flex sm:justify-between">
                          <div className="sm:flex">
                            <p className="flex items-center text-sm text-gray-500">
                              {hospital.address}, {hospital.city}
                            </p>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div className="flex space-x-2">
                      <Link
                        to={`/hospitals/${hospital.hospitalId}`}
                        className="text-blue-600 hover:text-blue-800 text-sm font-medium"
                      >
                        View
                      </Link>
                      {isAdmin && (
                        <>
                          <Link
                            to={`/hospitals/${hospital.hospitalId}/edit`}
                            className="text-green-600 hover:text-green-800 text-sm font-medium"
                          >
                            Edit
                          </Link>
                          <button
                            onClick={() => handleDelete(hospital.hospitalId)}
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

