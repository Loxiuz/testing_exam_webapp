import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { wardService } from '../../services/wardService';
import { Ward } from '../../types';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { ErrorMessage } from '../../components/common/ErrorMessage';
import { useAuth } from '../../context/AuthContext';

export const WardList: React.FC = () => {
  const [wards, setWards] = useState<Ward[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { role } = useAuth();
  const navigate = useNavigate();
  const isAdmin = role === 'ADMIN';

  useEffect(() => {
    loadWards();
  }, []);

  const loadWards = async () => {
    try {
      setLoading(true);
      const data = await wardService.getAll();
      console.log('Loaded wards:', data); // Debug log
      setWards(data || []);
      setError(null);
    } catch (err: any) {
      console.error('Error loading wards:', err); // Debug log
      setError(err.response?.data?.message || err.message || 'Failed to load wards');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: string) => {
    if (!window.confirm('Are you sure you want to delete this ward?')) {
      return;
    }

    try {
      await wardService.delete(id);
      loadWards();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to delete ward');
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
              <h1 className="text-3xl font-bold text-gray-900">Wards</h1>
              <p className="mt-1 text-sm text-gray-500">Manage ward records</p>
            </div>
            {isAdmin && (
              <button
                onClick={() => navigate('/wards/new')}
                className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-md shadow-sm"
              >
                Add New Ward
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Content Section */}
      <div className="bg-white shadow rounded-lg">
        <div className="px-4 py-5 sm:px-6">
          {error && <ErrorMessage message={error} onDismiss={() => setError(null)} />}

      {wards.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-gray-500 text-lg">No wards found.</p>
          {isAdmin && (
            <button
              onClick={() => navigate('/wards/new')}
              className="mt-4 text-blue-600 hover:text-blue-800"
            >
              Create your first ward
            </button>
          )}
        </div>
      ) : (
        <div className="bg-white shadow overflow-hidden sm:rounded-md">
          <ul className="divide-y divide-gray-200">
            {wards.map((ward) => (
              <li key={ward.wardId}>
                <div className="px-4 py-4 sm:px-6 hover:bg-gray-50">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center">
                      <div>
                        <Link
                          to={`/wards/${ward.wardId}`}
                          className="text-lg font-medium text-blue-600 hover:text-blue-800"
                        >
                          {ward.type} Ward
                        </Link>
                        <div className="mt-2 flex flex-wrap items-center gap-x-4 gap-y-1">
                          <span className="text-sm text-gray-500">
                            <span className="font-medium">Max Capacity:</span> {ward.maxCapacity}
                          </span>
                          {ward.hospitals && ward.hospitals.length > 0 && (
                            <span className="text-sm text-gray-500">
                              <span className="font-medium">Hospitals:</span>{' '}
                              {ward.hospitals.map((hospital, index) => (
                                <span key={hospital.hospitalId}>
                                  {index > 0 && ', '}
                                  <Link
                                    to={`/hospitals/${hospital.hospitalId}`}
                                    className="text-blue-600 hover:text-blue-800"
                                  >
                                    {hospital.hospitalName} - {hospital.city}
                                  </Link>
                                </span>
                              ))}
                            </span>
                          )}
                        </div>
                      </div>
                    </div>
                    <div className="flex space-x-2">
                      <Link
                        to={`/wards/${ward.wardId}`}
                        className="text-blue-600 hover:text-blue-800 text-sm font-medium"
                      >
                        View
                      </Link>
                      {isAdmin && (
                        <>
                          <Link
                            to={`/wards/${ward.wardId}/edit`}
                            className="text-green-600 hover:text-green-800 text-sm font-medium"
                          >
                            Edit
                          </Link>
                          <button
                            onClick={() => handleDelete(ward.wardId)}
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

