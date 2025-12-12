import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { wardService } from '../../services/wardService';
import { hospitalService } from '../../services/hospitalService';
import { WardRequest, WardType, Hospital } from '../../types';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { ErrorMessage } from '../../components/common/ErrorMessage';

export const WardForm: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const isEdit = !!id;
  const [loading, setLoading] = useState(isEdit);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [hospitals, setHospitals] = useState<Hospital[]>([]);
  const [loadingOptions, setLoadingOptions] = useState(true);

  const [formData, setFormData] = useState<WardRequest>({
    type: WardType.GENERAL_MEDICINE,
    maxCapacity: 1,
    hospitalIds: []
  });

  useEffect(() => {
    loadOptions();
    if (isEdit && id) {
      loadWard(id);
    }
  }, [id, isEdit]);

  const loadOptions = async () => {
    try {
      setLoadingOptions(true);
      const hospitalsData = await hospitalService.getAll();
      setHospitals(hospitalsData);
    } catch (err: any) {
      console.error('Failed to load hospitals:', err);
    } finally {
      setLoadingOptions(false);
    }
  };

  const loadWard = async (wardId: string) => {
    try {
      setLoading(true);
      const ward = await wardService.getById(wardId);
      setFormData({
        type: ward.type,
        maxCapacity: ward.maxCapacity,
        hospitalIds: ward.hospitals?.map(h => h.hospitalId) || []
      });
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load ward');
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
        await wardService.update(id, formData);
      } else {
        await wardService.create(formData);
      }
      navigate('/wards');
    } catch (err: any) {
      setError(err.response?.data?.message || `Failed to ${isEdit ? 'update' : 'create'} ward`);
    } finally {
      setSaving(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    if (name === 'maxCapacity') {
      setFormData(prev => ({ ...prev, [name]: parseInt(value, 10) }));
    } else {
      setFormData(prev => ({ ...prev, [name]: value }));
    }
  };

  const handleHospitalChange = (hospitalId: string, checked: boolean) => {
    setFormData(prev => {
      const currentIds = prev.hospitalIds || [];
      if (checked) {
        return { ...prev, hospitalIds: [...currentIds, hospitalId] };
      } else {
        return { ...prev, hospitalIds: currentIds.filter(id => id !== hospitalId) };
      }
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
        <Link to="/wards" className="text-blue-600 hover:text-blue-800">
          ← Back to Wards
        </Link>
      </div>

      <div className="bg-white shadow sm:rounded-lg">
        <div className="px-4 py-5 sm:p-6">
          <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">
            {isEdit ? 'Edit Ward' : 'Create New Ward'}
          </h3>

          {error && <ErrorMessage message={error} onDismiss={() => setError(null)} />}

          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label htmlFor="type" className="block text-sm font-medium text-gray-700">
                Ward Type *
              </label>
              <select
                name="type"
                id="type"
                required
                value={formData.type}
                onChange={handleChange}
                className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
              >
                <option value={WardType.GENERAL_MEDICINE}>General Medicine</option>
                <option value={WardType.CARDIOLOGY}>Cardiology</option>
                <option value={WardType.NEUROLOGY}>Neurology</option>
              </select>
            </div>

            <div>
              <label htmlFor="maxCapacity" className="block text-sm font-medium text-gray-700">
                Max Capacity *
              </label>
              <input
                type="number"
                name="maxCapacity"
                id="maxCapacity"
                required
                min="1"
                value={formData.maxCapacity}
                onChange={handleChange}
                className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Associated Hospitals
              </label>
              <div className="space-y-2 max-h-48 overflow-y-auto border border-gray-300 rounded-md p-3">
                {hospitals.map((hospital) => (
                  <label key={hospital.hospitalId} className="flex items-center">
                    <input
                      type="checkbox"
                      checked={formData.hospitalIds?.includes(hospital.hospitalId) || false}
                      onChange={(e) => handleHospitalChange(hospital.hospitalId, e.target.checked)}
                      disabled={loadingOptions}
                      className="mr-2"
                    />
                    <span className="text-sm text-gray-700">
                      {hospital.hospitalName} - {hospital.city}
                    </span>
                  </label>
                ))}
                {hospitals.length === 0 && (
                  <p className="text-sm text-gray-500">No hospitals available</p>
                )}
              </div>
            </div>

            <div className="flex justify-end space-x-3">
              <Link
                to="/wards"
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

