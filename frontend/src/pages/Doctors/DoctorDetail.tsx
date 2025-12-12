import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { doctorService } from '../../services/doctorService';
import { Doctor } from '../../types';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { ErrorMessage } from '../../components/common/ErrorMessage';
import { useAuth } from '../../context/AuthContext';

export const DoctorDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [doctor, setDoctor] = useState<Doctor | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { role } = useAuth();
  const navigate = useNavigate();
  const isAdmin = role === 'ADMIN';

  useEffect(() => {
    if (id) {
      loadDoctor(id);
    }
  }, [id]);

  const loadDoctor = async (doctorId: string) => {
    try {
      setLoading(true);
      const data = await doctorService.getById(doctorId);
      setDoctor(data);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load doctor');
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

  if (error || !doctor) {
    return (
      <div>
        <ErrorMessage message={error || 'Doctor not found'} />
        <Link to="/doctors" className="text-blue-600 hover:text-blue-800 mt-4 inline-block">
          Back to Doctors
        </Link>
      </div>
    );
  }

  return (
    <div>
      <div className="mb-6">
        <Link to="/doctors" className="text-blue-600 hover:text-blue-800">
          ← Back to Doctors
        </Link>
      </div>

      <div className="bg-white shadow overflow-hidden sm:rounded-lg">
        <div className="px-4 py-5 sm:px-6 flex justify-between items-center">
          <div>
            <h3 className="text-lg leading-6 font-medium text-gray-900">{doctor.doctorName}</h3>
            <p className="mt-1 max-w-2xl text-sm text-gray-500">Doctor Details</p>
          </div>
          {isAdmin && (
            <div className="flex space-x-2">
              <Link
                to={`/doctors/${doctor.doctorId}/edit`}
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
              <dt className="text-sm font-medium text-gray-500">Doctor ID</dt>
              <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">{doctor.doctorId}</dd>
            </div>
            <div className="bg-white px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
              <dt className="text-sm font-medium text-gray-500">Name</dt>
              <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">{doctor.doctorName}</dd>
            </div>
            <div className="bg-gray-50 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
              <dt className="text-sm font-medium text-gray-500">Speciality</dt>
              <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">{doctor.speciality}</dd>
            </div>
            {doctor.hospital && (
              <div className="bg-white px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                <dt className="text-sm font-medium text-gray-500">Hospital</dt>
                <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">
                  <Link
                    to={`/hospitals/${doctor.hospital.hospitalId}`}
                    className="text-blue-600 hover:text-blue-800"
                  >
                    {doctor.hospital.hospitalName} - {doctor.hospital.city}
                  </Link>
                </dd>
              </div>
            )}
            {doctor.ward && (
              <div className="bg-gray-50 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                <dt className="text-sm font-medium text-gray-500">Ward</dt>
                <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">
                  <Link
                    to={`/wards/${doctor.ward.wardId}`}
                    className="text-blue-600 hover:text-blue-800"
                  >
                    {doctor.ward.type} Ward
                  </Link>
                  {' '}(Capacity: {doctor.ward.maxCapacity})
                </dd>
              </div>
            )}
          </dl>
        </div>
      </div>
    </div>
  );
};

