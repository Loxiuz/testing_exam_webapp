import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { appointmentService } from '../../services/appointmentService';
import { Appointment } from '../../types';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { ErrorMessage } from '../../components/common/ErrorMessage';
import { useAuth } from '../../context/AuthContext';
import { format } from 'date-fns';

export const AppointmentList: React.FC = () => {
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { role } = useAuth();
  const navigate = useNavigate();
  const isAdmin = role === 'ADMIN';

  useEffect(() => {
    loadAppointments();
  }, []);

  const loadAppointments = async () => {
    try {
      setLoading(true);
      const data = await appointmentService.getAll();
      setAppointments(data);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load appointments');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: string) => {
    if (!window.confirm('Are you sure you want to delete this appointment?')) {
      return;
    }

    try {
      await appointmentService.delete(id);
      loadAppointments();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to delete appointment');
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
              <h1 className="text-3xl font-bold text-gray-900">Appointments</h1>
              <p className="mt-1 text-sm text-gray-500">Manage appointment schedules</p>
            </div>
            <button
              onClick={() => navigate('/appointments/new')}
              className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-md shadow-sm"
            >
              Schedule New Appointment
            </button>
          </div>
        </div>
      </div>

      {/* Content Section */}
      <div className="bg-white shadow rounded-lg">
        <div className="px-4 py-5 sm:px-6">
          {error && <ErrorMessage message={error} onDismiss={() => setError(null)} />}

      {appointments.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-gray-500 text-lg">No appointments found.</p>
          <button
            onClick={() => navigate('/appointments/new')}
            className="mt-4 text-blue-600 hover:text-blue-800"
          >
            Schedule your first appointment
          </button>
        </div>
      ) : (
        <div className="bg-white shadow overflow-hidden sm:rounded-md">
          <ul className="divide-y divide-gray-200">
            {appointments.map((appointment) => (
              <li key={appointment.appointmentId}>
                <div className="px-4 py-4 sm:px-6 hover:bg-gray-50">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center">
                      <div>
                        <Link
                          to={`/appointments/${appointment.appointmentId}`}
                          className="text-lg font-medium text-blue-600 hover:text-blue-800"
                        >
                          Appointment on {format(new Date(appointment.appointmentDate), 'MMM dd, yyyy')}
                        </Link>
                        <div className="mt-2 sm:flex sm:justify-between">
                          <div className="sm:flex">
                            {appointment.patient && (
                              <p className="flex items-center text-sm text-gray-500">
                                Patient: {appointment.patient.patientName}
                              </p>
                            )}
                            {appointment.doctor && (
                              <p className="mt-2 flex items-center text-sm text-gray-500 sm:mt-0 sm:ml-6">
                                Doctor: {appointment.doctor.doctorName}
                              </p>
                            )}
                            <p className="mt-2 flex items-center text-sm text-gray-500 sm:mt-0 sm:ml-6">
                              Status: <span className={`ml-1 px-2 py-1 rounded text-xs ${
                                appointment.status === 'SCHEDULED' ? 'bg-yellow-100 text-yellow-800' :
                                appointment.status === 'COMPLETED' ? 'bg-green-100 text-green-800' :
                                'bg-red-100 text-red-800'
                              }`}>
                                {appointment.status}
                              </span>
                            </p>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div className="flex space-x-2">
                      <Link
                        to={`/appointments/${appointment.appointmentId}`}
                        className="text-blue-600 hover:text-blue-800 text-sm font-medium"
                      >
                        View
                      </Link>
                      {isAdmin && (
                        <>
                          <Link
                            to={`/appointments/${appointment.appointmentId}/edit`}
                            className="text-green-600 hover:text-green-800 text-sm font-medium"
                          >
                            Edit
                          </Link>
                          <button
                            onClick={() => handleDelete(appointment.appointmentId)}
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

