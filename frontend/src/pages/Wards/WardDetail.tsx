import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { wardService } from '../../services/wardService';
import { Ward, Hospital } from '../../types';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { ErrorMessage } from '../../components/common/ErrorMessage';
import { useAuth } from '../../context/AuthContext';

export const WardDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [ward, setWard] = useState<Ward | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { role } = useAuth();
  const navigate = useNavigate();
  const isAdmin = role === 'ADMIN';

  useEffect(() => {
    if (id) {
      loadWard(id);
    }
  }, [id]);

  const loadWard = async (wardId: string) => {
    try {
      setLoading(true);
      const data = await wardService.getById(wardId);
      setWard(data);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load ward');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <LoadingSpinner size="large" />
      </div>
    );
  }

  if (error || !ward) {
    return (
      <div>
        <ErrorMessage message={error || 'Ward not found'} />
        <Link to="/wards" className="text-blue-600 hover:text-blue-800 mt-4 inline-block">
          Back to Wards
        </Link>
      </div>
    );
  }

  return (
    <div>
      <div className="mb-6">
        <Link to="/wards" className="text-blue-600 hover:text-blue-800">
          ← Back to Wards
        </Link>
      </div>

      <div className="bg-white shadow overflow-hidden sm:rounded-lg">
        <div className="px-4 py-5 sm:px-6 flex justify-between items-center">
          <div>
            <h3 className="text-lg leading-6 font-medium text-gray-900">{ward.type} Ward</h3>
            <p className="mt-1 max-w-2xl text-sm text-gray-500">Ward Details</p>
          </div>
          {isAdmin && (
            <div className="flex space-x-2">
              <Link
                to={`/wards/${ward.wardId}/edit`}
                className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-md text-sm"
              >
                Edit
              </Link>
            </div>
          )}
        </div>
        <div className="border-t border-gray-200">
          <dl>
            <div className="bg-gray-50 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
              <dt className="text-sm font-medium text-gray-500">Ward ID</dt>
              <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">{ward.wardId}</dd>
            </div>
            <div className="bg-white px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
              <dt className="text-sm font-medium text-gray-500">Type</dt>
              <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">{ward.type}</dd>
            </div>
            <div className="bg-gray-50 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
              <dt className="text-sm font-medium text-gray-500">Max Capacity</dt>
              <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">{ward.maxCapacity}</dd>
            </div>
            {ward.hospitals && ward.hospitals.length > 0 && (
              <div className="bg-white px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                <dt className="text-sm font-medium text-gray-500">Hospitals</dt>
                <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">
                  <ul className="list-disc list-inside space-y-1">
                    {ward.hospitals.map((hospital) => (
                      <li key={hospital.hospitalId}>
                        <Link
                          to={`/hospitals/${hospital.hospitalId}`}
                          className="text-blue-600 hover:text-blue-800"
                        >
                          {hospital.hospitalName}
                        </Link>
                        {' '}- {hospital.city}
                      </li>
                    ))}
                  </ul>
                </dd>
              </div>
            )}
          </dl>
        </div>
      </div>
    </div>
  );
};

