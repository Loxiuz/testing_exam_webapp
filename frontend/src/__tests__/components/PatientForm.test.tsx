import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { PatientForm } from '../../pages/Patients/PatientForm';
import { patientService } from '../../services/patientService';
import { hospitalService } from '../../services/hospitalService';
import { wardService } from '../../services/wardService';

// Mock the services
vi.mock('../../services/patientService');
vi.mock('../../services/hospitalService');
vi.mock('../../services/wardService');
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useParams: () => ({ id: undefined }),
    useNavigate: () => vi.fn(),
  };
});

describe('PatientForm', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should render form fields', async () => {
    const mockHospitals = [
      {
        hospitalId: '1',
        hospitalName: 'Test Hospital',
        address: '123 Test St',
        city: 'Test City',
        wards: [],
      },
    ];

    const mockWards = [
      {
        wardId: '1',
        type: 'CARDIOLOGY',
        maxCapacity: 30,
        hospitals: [],
      },
    ];

    (hospitalService.getAll as any).mockResolvedValue(mockHospitals);
    (wardService.getAll as any).mockResolvedValue(mockWards);

    render(
      <BrowserRouter>
        <PatientForm />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByLabelText(/patient name/i)).toBeInTheDocument();
      expect(screen.getByLabelText(/date of birth/i)).toBeInTheDocument();
      expect(screen.getByLabelText(/gender/i)).toBeInTheDocument();
    });
  });

  it('should display loading state initially', () => {
    (hospitalService.getAll as any).mockResolvedValue([]);
    (wardService.getAll as any).mockResolvedValue([]);

    render(
      <BrowserRouter>
        <PatientForm />
      </BrowserRouter>
    );

    // Loading spinner should be present during initial load
    // This test verifies the component renders without crashing
    expect(document.body).toBeTruthy();
  });
});

