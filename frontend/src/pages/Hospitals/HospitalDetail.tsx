import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { hospitalService } from '../../services/hospitalService';
import { Hospital } from '../../types';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { ErrorMessage } from '../../components/common/ErrorMessage';
import { useAuth } from '../../context/AuthContext';
import { WeatherWidget } from '../../components/widgets/WeatherWidget';

export const HospitalDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [hospital, setHospital] = useState<Hospital | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { role } = useAuth();
  const navigate = useNavigate();
  const isAdmin = role === 'ADMIN';

  useEffect(() => {
    if (id) {
      loadHospital(id);
    }
  }, [id]);

  const loadHospital = async (hospitalId: string) => {
    try {
      setLoading(true);
      const data = await hospitalService.getById(hospitalId);
      setHospital(data);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load hospital');
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

  if (error || !hospital) {
    return (
      <div>
        <ErrorMessage message={error || 'Hospital not found'} />
        <Link to="/hospitals" className="text-blue-600 hover:text-blue-800 mt-4 inline-block">
          Back to Hospitals
        </Link>
      </div>
    );
  }

  return (
    <div>
      <div className="mb-6">
        <Link to="/hospitals" className="text-blue-600 hover:text-blue-800">
          ← Back to Hospitals
        </Link>
      </div>

      <div className="bg-white shadow overflow-hidden sm:rounded-lg">
        <div className="px-4 py-5 sm:px-6 flex justify-between items-center">
          <div>
            <h3 className="text-lg leading-6 font-medium text-gray-900">{hospital.hospitalName}</h3>
            <p className="mt-1 max-w-2xl text-sm text-gray-500">Hospital Details</p>
          </div>
          {isAdmin && (
            <div className="flex space-x-2">
              <Link
                to={`/hospitals/${hospital.hospitalId}/edit`}
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
              <dt className="text-sm font-medium text-gray-500">Hospital ID</dt>
              <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">{hospital.hospitalId}</dd>
            </div>
            <div className="bg-white px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
              <dt className="text-sm font-medium text-gray-500">Name</dt>
              <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">{hospital.hospitalName}</dd>
            </div>
            <div className="bg-gray-50 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
              <dt className="text-sm font-medium text-gray-500">Address</dt>
              <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">{hospital.address}</dd>
            </div>
            <div className="bg-white px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
              <dt className="text-sm font-medium text-gray-500">City</dt>
              <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">{hospital.city}</dd>
            </div>
            {hospital.wards && hospital.wards.length > 0 && (
              <div className="bg-gray-50 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                <dt className="text-sm font-medium text-gray-500">Wards</dt>
                <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">
                  <ul className="list-disc list-inside space-y-1">
                    {hospital.wards.map((ward) => (
                      <li key={ward.wardId}>
                        <Link
                          to={`/wards/${ward.wardId}`}
                          className="text-blue-600 hover:text-blue-800"
                        >
                          {ward.type} Ward
                        </Link>
                        {' '}(Capacity: {ward.maxCapacity})
                      </li>
                    ))}
                  </ul>
                </dd>
              </div>
            )}
          </dl>
        </div>
      </div>

      {/* Weather Widget Section */}
      {hospital.city && (
        <div className="mt-6">
          <WeatherWidget city={hospital.city} />
        </div>
      )}
    </div>
  );
};

