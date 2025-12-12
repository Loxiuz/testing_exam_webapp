// TEMPORARY TEST - Replace main.tsx content with this to test if React works
import { createRoot } from 'react-dom/client'
import './index.css'

const rootElement = document.getElementById('root');

if (!rootElement) {
  throw new Error('Root element not found');
}

const TestComponent = () => {
  return (
    <div style={{ 
      padding: '40px', 
      backgroundColor: '#e0f2fe', 
      minHeight: '100vh',
      fontFamily: 'sans-serif'
    }}>
      <h1 style={{ color: '#0369a1', fontSize: '32px' }}>✅ React is Working!</h1>
      <p style={{ color: '#0c4a6e', fontSize: '18px', marginTop: '20px' }}>
        If you see this message, React is rendering correctly.
      </p>
      <p style={{ color: '#0c4a6e', fontSize: '16px', marginTop: '10px' }}>
        The white page issue is likely a routing or component error.
      </p>
      <p style={{ color: '#0c4a6e', fontSize: '14px', marginTop: '10px' }}>
        Check the browser console (F12) for JavaScript errors.
      </p>
    </div>
  );
};

const root = createRoot(rootElement);
root.render(<TestComponent />);

