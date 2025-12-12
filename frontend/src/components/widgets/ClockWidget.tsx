import React, { useEffect, useState } from 'react';
import { timeService } from '../../services/timeService';
import { Time } from '../../types';
import { LoadingSpinner } from '../common/LoadingSpinner';
import { ErrorMessage } from '../common/ErrorMessage';

interface ClockWidgetProps {
  timezone?: string;
  className?: string;
}

export const ClockWidget: React.FC<ClockWidgetProps> = ({ timezone, className = '' }) => {
  const [time, setTime] = useState<Time | null>(null);
  const [currentTime, setCurrentTime] = useState<string>('');
  const [currentDate, setCurrentDate] = useState<string>('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadTime = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await timeService.getCurrentTime(timezone);
      setTime(data);
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to load time');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadTime();
  }, [timezone]);

  // Auto-update time every second using browser's local time
  useEffect(() => {
    const updateTime = () => {
      if (time?.timezone) {
        try {
          const now = new Date();
          const timeStr = now.toLocaleTimeString('en-US', {
            hour12: false,
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
            timeZone: time.timezone
          });
          const dateStr = now.toLocaleDateString('en-US', {
            weekday: 'long',
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            timeZone: time.timezone
          });
          setCurrentTime(timeStr);
          setCurrentDate(dateStr);
        } catch (e) {
          // Fallback to simple time display
          const now = new Date();
          setCurrentTime(now.toLocaleTimeString());
          setCurrentDate(now.toLocaleDateString());
        }
      }
    };

    // Update immediately
    updateTime();

    // Then update every second
    const interval = setInterval(updateTime, 1000);
    return () => clearInterval(interval);
  }, [time]);

  const getDayName = (dayOfWeek: number) => {
    const days = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
    return days[dayOfWeek] || '';
  };

  if (loading) {
    return (
      <div className={`bg-white shadow rounded-lg p-6 ${className}`}>
        <div className="flex justify-center items-center h-32">
          <LoadingSpinner size="small" />
        </div>
      </div>
    );
  }

  if (error || !time) {
    return (
      <div className={`bg-white shadow rounded-lg p-6 ${className}`}>
        <ErrorMessage message={error || 'Time data unavailable'} />
      </div>
    );
  }

  return (
    <div className={`bg-gradient-to-br from-indigo-500 to-indigo-600 text-white shadow-lg rounded-lg p-6 ${className}`}>
      <div className="text-center">
        <h3 className="text-lg font-semibold mb-2">Current Time</h3>
        <div className="text-5xl font-bold mb-2 font-mono">
          {currentTime || '--:--:--'}
        </div>
        <p className="text-indigo-100 text-sm mb-1">{currentDate}</p>
        <p className="text-indigo-200 text-xs">
          {time.timezone} ({time.abbreviation})
        </p>
        {time.dayOfWeek && (
          <p className="text-indigo-200 text-xs mt-2">
            {getDayName(time.dayOfWeek)} • Day {time.dayOfYear} of the year
          </p>
        )}
      </div>
    </div>
  );
};

