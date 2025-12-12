import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { doctorService } from '../../services/doctorService';
import { wardService } from '../../services/wardService';
import { hospitalService } from '../../services/hospitalService';
import { DoctorRequest, DoctorSpecialityType, Ward, Hospital } from '../../types';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { ErrorMessage } from '../../components/common/ErrorMessage';

export const DoctorForm: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const isEdit = !!id;
  const [loading, setLoading] = useState(isEdit);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [wards, setWards] = useState<Ward[]>([]);
  const [hospitals, setHospitals] = useState<Hospital[]>([]);
  const [loadingOptions, setLoadingOptions] = useState(true);

  const [formData, setFormData] = useState<DoctorRequest>({
    doctorName: '',
    speciality: DoctorSpecialityType.GENERAL_MEDICINE,
    wardId: '',
    hospitalId: ''
  });
  
  // Filter wards based on selected hospital
  const availableWards = React.useMemo(() => {
    if (!formData.hospitalId) {
      return wards;
    }
    const selectedHospital = hospitals.find(h => h.hospitalId === formData.hospitalId);
    if (!selectedHospital || !selectedHospital.wards) {
      return [];
    }
    return selectedHospital.wards;
  }, [formData.hospitalId, hospitals, wards]);

  useEffect(() => {
    loadOptions();
    if (isEdit && id) {
      loadDoctor(id);
    }
  }, [id, isEdit]);

  const loadOptions = async () => {
    try {
      setLoadingOptions(true);
      const [wardsData, hospitalsData] = await Promise.all([
        wardService.getAll(),
        hospitalService.getAll()
      ]);
      setWards(wardsData);
      setHospitals(hospitalsData);
    } catch (err: any) {
      console.error('Failed to load options:', err);
    } finally {
      setLoadingOptions(false);
    }
  };

  const loadDoctor = async (doctorId: string) => {
    try {
      setLoading(true);
      const doctor = await doctorService.getById(doctorId);
      setFormData({
        doctorName: doctor.doctorName,
        speciality: doctor.speciality,
        wardId: doctor.ward?.wardId || '',
        hospitalId: doctor.hospital?.hospitalId || ''
      });
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.error || err.response?.data?.message || 'Failed to load doctor');
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
        await doctorService.update(id, formData);
      } else {
        await doctorService.create(formData);
      }
      navigate('/doctors');
    } catch (err: any) {
      const errorMessage = err.response?.data?.error || err.response?.data?.message || `Failed to ${isEdit ? 'update' : 'create'} doctor`;
      setError(errorMessage);
    } finally {
      setSaving(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => {
      const updated = { ...prev, [name]: value };
      // If hospital changes, clear ward selection if it's not valid for the new hospital
      if (name === 'hospitalId') {
        const selectedHospital = hospitals.find(h => h.hospitalId === value);
        if (selectedHospital && prev.wardId) {
          const wardBelongsToHospital = selectedHospital.wards?.some(
            (w: Ward) => w.wardId === prev.wardId
          );
          if (!wardBelongsToHospital) {
            updated.wardId = '';
          }
        } else if (!selectedHospital) {
          updated.wardId = '';
        }
      }
      return updated;
    });
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
        <Link to="/doctors" className="text-blue-600 hover:text-blue-800">
          ← Back to Doctors
        </Link>
      </div>

      <div className="bg-white shadow sm:rounded-lg">
        <div className="px-4 py-5 sm:p-6">
          <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">
            {isEdit ? 'Edit Doctor' : 'Create New Doctor'}
          </h3>

          {error && <ErrorMessage message={error} onDismiss={() => setError(null)} />}

          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label htmlFor="doctorName" className="block text-sm font-medium text-gray-700">
                Doctor Name *
              </label>
              <input
                type="text"
                name="doctorName"
                id="doctorName"
                required
                value={formData.doctorName}
                onChange={handleChange}
                className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
              />
            </div>

            <div>
              <label htmlFor="speciality" className="block text-sm font-medium text-gray-700">
                Speciality *
              </label>
              <select
                name="speciality"
                id="speciality"
                required
                value={formData.speciality}
                onChange={handleChange}
                className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
              >
                <option value={DoctorSpecialityType.GENERAL_MEDICINE}>General Medicine</option>
                <option value={DoctorSpecialityType.CARDIOLOGY}>Cardiology</option>
                <option value={DoctorSpecialityType.NEUROLOGY}>Neurology</option>
                <option value={DoctorSpecialityType.SURGERY}>Surgery</option>
              </select>
            </div>

            <div>
              <label htmlFor="hospitalId" className="block text-sm font-medium text-gray-700">
                Hospital
              </label>
              <select
                name="hospitalId"
                id="hospitalId"
                value={formData.hospitalId}
                onChange={handleChange}
                disabled={loadingOptions}
                className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 disabled:bg-gray-100"
              >
                <option value="">Select a hospital</option>
                {hospitals.map((hospital) => (
                  <option key={hospital.hospitalId} value={hospital.hospitalId}>
                    {hospital.hospitalName} - {hospital.city}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label htmlFor="wardId" className="block text-sm font-medium text-gray-700">
                Ward
              </label>
              <select
                name="wardId"
                id="wardId"
                value={formData.wardId}
                onChange={handleChange}
                disabled={loadingOptions || !formData.hospitalId}
                className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 disabled:bg-gray-100"
              >
                <option value="">
                  {!formData.hospitalId 
                    ? 'Select a hospital first' 
                    : availableWards.length === 0
                    ? 'No wards available for this hospital'
                    : 'Select a ward'}
                </option>
                {availableWards.map((ward: Ward) => (
                  <option key={ward.wardId} value={ward.wardId}>
                    {ward.type} Ward (Capacity: {ward.maxCapacity})
                  </option>
                ))}
              </select>
              {formData.hospitalId && availableWards.length === 0 && (
                <p className="mt-1 text-sm text-yellow-600">
                  This hospital has no wards assigned. Please assign wards to the hospital first.
                </p>
              )}
            </div>

            <div className="flex justify-end space-x-3">
              <Link
                to="/doctors"
                className="bg-gray-300 hover:bg-gray-400 text-gray-800 px-4 py-2 rounded-md"
              >
                Cancel
              </Link>
              <button
                type="submit"
                disabled={saving}
                className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-md disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {saving ? <LoadingSpinner size="small" /> : (isEdit ? 'Update' : 'Create')}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

