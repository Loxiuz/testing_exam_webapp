import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { appointmentService } from '../../services/appointmentService';
import { patientService } from '../../services/patientService';
import { doctorService } from '../../services/doctorService';
import { nurseService } from '../../services/nurseService';
import { AppointmentRequest, AppointmentStatusType, Patient, Doctor, Nurse } from '../../types';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { ErrorMessage } from '../../components/common/ErrorMessage';

export const AppointmentForm: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const isEdit = !!id;
  const [loading, setLoading] = useState(isEdit);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [patients, setPatients] = useState<Patient[]>([]);
  const [doctors, setDoctors] = useState<Doctor[]>([]);
  const [nurses, setNurses] = useState<Nurse[]>([]);
  const [loadingOptions, setLoadingOptions] = useState(true);

  const [formData, setFormData] = useState<AppointmentRequest>({
    appointmentDate: '',
    reason: '',
    status: AppointmentStatusType.SCHEDULED,
    patientId: '',
    doctorId: '',
    nurseId: ''
  });

  useEffect(() => {
    loadOptions();
    if (isEdit && id) {
      loadAppointment(id);
    }
  }, [id, isEdit]);

  const loadOptions = async () => {
    try {
      setLoadingOptions(true);
      const [patientsData, doctorsData, nursesData] = await Promise.all([
        patientService.getAll(),
        doctorService.getAll(),
        nurseService.getAll()
      ]);
      setPatients(patientsData);
      setDoctors(doctorsData);
      setNurses(nursesData);
    } catch (err: any) {
      console.error('Failed to load options:', err);
    } finally {
      setLoadingOptions(false);
    }
  };

  const loadAppointment = async (appointmentId: string) => {
    try {
      setLoading(true);
      const appointment = await appointmentService.getById(appointmentId);
      setFormData({
        appointmentDate: appointment.appointmentDate.split('T')[0], // Extract date part
        reason: appointment.reason || '',
        status: appointment.status,
        patientId: appointment.patient?.patientId || '',
        doctorId: appointment.doctor?.doctorId || '',
        nurseId: appointment.nurse?.nurseId || ''
      });
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load appointment');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSaving(true);

    try {
      if (isEdit && id) {
        await appointmentService.update(id, formData);
      } else {
        await appointmentService.create(formData);
      }
      navigate('/appointments');
    } catch (err: any) {
      setError(err.response?.data?.message || `Failed to ${isEdit ? 'update' : 'create'} appointment`);
    } finally {
      setSaving(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <LoadingSpinner size="large" />
      </div>
    );
  }

  return (
    <div>
      <div className="mb-6">
        <Link to="/appointments" className="text-blue-600 hover:text-blue-800">
          ← Back to Appointments
        </Link>
      </div>

      <div className="bg-white shadow sm:rounded-lg">
        <div className="px-4 py-5 sm:p-6">
          <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">
            {isEdit ? 'Edit Appointment' : 'Schedule New Appointment'}
          </h3>

          {error && <ErrorMessage message={error} onDismiss={() => setError(null)} />}

          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label htmlFor="appointmentDate" className="block text-sm font-medium text-gray-700">
                Appointment Date *
              </label>
              <input
                type="date"
                name="appointmentDate"
                id="appointmentDate"
                required
                value={formData.appointmentDate}
                onChange={handleChange}
                className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
              />
            </div>

            <div>
              <label htmlFor="status" className="block text-sm font-medium text-gray-700">
                Status *
              </label>
              <select
                name="status"
                id="status"
                required
                value={formData.status}
                onChange={handleChange}
                className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
              >
                <option value={AppointmentStatusType.SCHEDULED}>Scheduled</option>
                <option value={AppointmentStatusType.COMPLETED}>Completed</option>
                <option value={AppointmentStatusType.CANCELLED}>Cancelled</option>
              </select>
            </div>

            <div>
              <label htmlFor="reason" className="block text-sm font-medium text-gray-700">
                Reason
              </label>
              <textarea
                name="reason"
                id="reason"
                rows={3}
                value={formData.reason}
                onChange={handleChange}
                className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                placeholder="Enter appointment reason"
              />
            </div>

            <div>
              <label htmlFor="patientId" className="block text-sm font-medium text-gray-700">
                Patient
              </label>
              <select
                name="patientId"
                id="patientId"
                value={formData.patientId}
                onChange={handleChange}
                disabled={loadingOptions}
                className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 disabled:bg-gray-100"
              >
                <option value="">Select a patient</option>
                {patients.map((patient) => (
                  <option key={patient.patientId} value={patient.patientId}>
                    {patient.patientName} {patient.dateOfBirth ? `(DOB: ${new Date(patient.dateOfBirth).toLocaleDateString()})` : ''}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label htmlFor="doctorId" className="block text-sm font-medium text-gray-700">
                Doctor
              </label>
              <select
                name="doctorId"
                id="doctorId"
                value={formData.doctorId}
                onChange={handleChange}
                disabled={loadingOptions}
                className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 disabled:bg-gray-100"
              >
                <option value="">Select a doctor</option>
                {doctors.map((doctor) => (
                  <option key={doctor.doctorId} value={doctor.doctorId}>
                    {doctor.doctorName} - {doctor.speciality}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label htmlFor="nurseId" className="block text-sm font-medium text-gray-700">
                Nurse
              </label>
              <select
                name="nurseId"
                id="nurseId"
                value={formData.nurseId}
                onChange={handleChange}
                disabled={loadingOptions}
                className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 disabled:bg-gray-100"
              >
                <option value="">Select a nurse (optional)</option>
                {nurses.map((nurse) => (
                  <option key={nurse.nurseId} value={nurse.nurseId}>
                    {nurse.nurseName}
                  </option>
                ))}
              </select>
            </div>

            <div className="flex justify-end space-x-3">
              <Link
                to="/appointments"
                className="bg-gray-300 hover:bg-gray-400 text-gray-800 px-4 py-2 rounded-md"
              >
                Cancel
              </Link>
              <button
                type="submit"
                disabled={saving}
                className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-md disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {saving ? <LoadingSpinner size="small" /> : (isEdit ? 'Update' : 'Schedule')}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

