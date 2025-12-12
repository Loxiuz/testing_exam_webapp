import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { ClockWidget } from '../../../components/widgets/ClockWidget';
import { timeService } from '../../../services/timeService';

vi.mock('../../../services/timeService');

describe('ClockWidget', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should render clock widget with time data', async () => {
    const mockTime = {
      datetime: '2024-12-12T12:00:00.000000+00:00',
      timezone: 'Europe/Copenhagen',
      abbreviation: 'CET',
      dayOfWeek: 4,
      dayOfYear: 347,
    };

    (timeService.getCurrentTime as any).mockResolvedValue(mockTime);

    render(<ClockWidget />);

    await waitFor(() => {
      expect(screen.getByText('Current Time')).toBeInTheDocument();
      expect(screen.getByText('Europe/Copenhagen')).toBeInTheDocument();
    });
  });

  it('should render clock widget for specific timezone', async () => {
    const mockTime = {
      datetime: '2024-12-12T07:00:00.000000-05:00',
      timezone: 'America/New_York',
      abbreviation: 'EST',
      dayOfWeek: 4,
      dayOfYear: 347,
    };

    (timeService.getCurrentTime as any).mockResolvedValue(mockTime);

    render(<ClockWidget timezone="America/New_York" />);

    await waitFor(() => {
      expect(timeService.getCurrentTime).toHaveBeenCalledWith('America/New_York');
      expect(screen.getByText('America/New_York')).toBeInTheDocument();
    });
  });

  it('should display loading state initially', () => {
    (timeService.getCurrentTime as any).mockImplementation(
      () => new Promise(() => {}) // Never resolves
    );

    render(<ClockWidget />);

    expect(document.querySelector('.animate-spin')).toBeInTheDocument();
  });

  it('should display error message on failure', async () => {
    const error = new Error('Failed to load time');
    (timeService.getCurrentTime as any).mockRejectedValue(error);

    render(<ClockWidget />);

    await waitFor(() => {
      expect(screen.getByText(/Failed to load time/i)).toBeInTheDocument();
    });
  });
});

