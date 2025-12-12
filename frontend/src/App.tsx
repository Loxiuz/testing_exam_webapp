import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { Layout } from './components/Layout/Layout';
import { ProtectedRoute } from './components/common/ProtectedRoute';
import { Login } from './pages/Login';
import { Dashboard } from './pages/Dashboard';
import { PatientList } from './pages/Patients/PatientList';
import { PatientDetail } from './pages/Patients/PatientDetail';
import { PatientForm } from './pages/Patients/PatientForm';
import { DoctorList } from './pages/Doctors/DoctorList';
import { DoctorDetail } from './pages/Doctors/DoctorDetail';
import { DoctorForm } from './pages/Doctors/DoctorForm';
import { NurseList } from './pages/Nurses/NurseList';
import { NurseDetail } from './pages/Nurses/NurseDetail';
import { NurseForm } from './pages/Nurses/NurseForm';
import { AppointmentList } from './pages/Appointments/AppointmentList';
import { AppointmentDetail } from './pages/Appointments/AppointmentDetail';
import { AppointmentForm } from './pages/Appointments/AppointmentForm';
import { HospitalList } from './pages/Hospitals/HospitalList';
import { HospitalDetail } from './pages/Hospitals/HospitalDetail';
import { HospitalForm } from './pages/Hospitals/HospitalForm';
import { WardList } from './pages/Wards/WardList';
import { WardDetail } from './pages/Wards/WardDetail';
import { WardForm } from './pages/Wards/WardForm';

function App() {
  console.log('App component rendering...');
  
  try {
    return (
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route
              path="/"
              element={
                <ProtectedRoute>
                  <Layout>
                    <Navigate to="/dashboard" replace />
                  </Layout>
                </ProtectedRoute>
              }
            />
            <Route
              path="/dashboard"
              element={
                <ProtectedRoute>
                  <Layout>
                    <Dashboard />
                  </Layout>
                </ProtectedRoute>
              }
            />
            <Route
              path="/patients"
              element={
                <ProtectedRoute>
                  <Layout>
                    <PatientList />
                  </Layout>
                </ProtectedRoute>
              }
            />
            <Route
              path="/patients/new"
              element={
                <ProtectedRoute>
                  <Layout>
                    <PatientForm />
                  </Layout>
                </ProtectedRoute>
              }
            />
            <Route
              path="/patients/:id"
              element={
                <ProtectedRoute>
                  <Layout>
                    <PatientDetail />
                  </Layout>
                </ProtectedRoute>
              }
            />
            <Route
              path="/patients/:id/edit"
              element={
                <ProtectedRoute>
                  <Layout>
                    <PatientForm />
                  </Layout>
                </ProtectedRoute>
              }
            />
            <Route
              path="/doctors"
              element={
                <ProtectedRoute>
                  <Layout>
                    <DoctorList />
                  </Layout>
                </ProtectedRoute>
              }
            />
            <Route
              path="/doctors/new"
              element={
                <ProtectedRoute>
                  <Layout>
                    <DoctorForm />
                  </Layout>
                </ProtectedRoute>
              }
            />
            <Route
              path="/doctors/:id"
              element={
                <ProtectedRoute>
                  <Layout>
                    <DoctorDetail />
                  </Layout>
                </ProtectedRoute>
              }
            />
            <Route
              path="/doctors/:id/edit"
              element={
                <ProtectedRoute>
                  <Layout>
                    <DoctorForm />
                  </Layout>
                </ProtectedRoute>
              }
            />
            <Route
              path="/nurses"
              element={
                <ProtectedRoute>
                  <Layout>
                    <NurseList />
                  </Layout>
                </ProtectedRoute>
              }
            />
            <Route
              path="/nurses/new"
              element={
                <ProtectedRoute>
                  <Layout>
                    <NurseForm />
                  </Layout>
                </ProtectedRoute>
              }
            />
            <Route
              path="/nurses/:id"
              element={
                <ProtectedRoute>
                  <Layout>
                    <NurseDetail />
                  </Layout>
                </ProtectedRoute>
              }
            />
            <Route
              path="/nurses/:id/edit"
              element={
                <ProtectedRoute>
                  <Layout>
                    <NurseForm />
                  </Layout>
                </ProtectedRoute>
              }
            />
            <Route
              path="/appointments"
              element={
                <ProtectedRoute>
                  <Layout>
                    <AppointmentList />
                  </Layout>
                </ProtectedRoute>
              }
            />
            <Route
              path="/appointments/new"
              element={
                <ProtectedRoute>
                  <Layout>
                    <AppointmentForm />
                  </Layout>
                </ProtectedRoute>
              }
            />
            <Route
              path="/appointments/:id"
              element={
                <ProtectedRoute>
                  <Layout>
                    <AppointmentDetail />
                  </Layout>
                </ProtectedRoute>
              }
            />
            <Route
              path="/appointments/:id/edit"
              element={
                <ProtectedRoute>
                  <Layout>
                    <AppointmentForm />
                  </Layout>
                </ProtectedRoute>
              }
            />
            <Route
              path="/hospitals"
              element={
                <ProtectedRoute>
                  <Layout>
                    <HospitalList />
                  </Layout>
                </ProtectedRoute>
              }
            />
            <Route
              path="/hospitals/new"
              element={
                <ProtectedRoute>
                  <Layout>
                    <HospitalForm />
                  </Layout>
                </ProtectedRoute>
              }
            />
            <Route
              path="/hospitals/:id"
              element={
                <ProtectedRoute>
                  <Layout>
                    <HospitalDetail />
                  </Layout>
                </ProtectedRoute>
              }
            />
            <Route
              path="/hospitals/:id/edit"
              element={
                <ProtectedRoute>
                  <Layout>
                    <HospitalForm />
                  </Layout>
                </ProtectedRoute>
              }
            />
            <Route
              path="/wards"
              element={
                <ProtectedRoute>
                  <Layout>
                    <WardList />
                  </Layout>
                </ProtectedRoute>
              }
            />
            <Route
              path="/wards/new"
              element={
                <ProtectedRoute>
                  <Layout>
                    <WardForm />
                  </Layout>
                </ProtectedRoute>
              }
            />
            <Route
              path="/wards/:id"
              element={
                <ProtectedRoute>
                  <Layout>
                    <WardDetail />
                  </Layout>
                </ProtectedRoute>
              }
            />
            <Route
              path="/wards/:id/edit"
              element={
                <ProtectedRoute>
                  <Layout>
                    <WardForm />
                  </Layout>
                </ProtectedRoute>
              }
            />
            <Route path="*" element={<Navigate to="/login" replace />} />
          </Routes>
        </BrowserRouter>
      </AuthProvider>
    );
  } catch (error) {
    console.error('App error:', error);
    return (
      <div style={{ padding: '20px', color: 'red' }}>
        <h1>Error loading application</h1>
        <pre>{String(error)}</pre>
      </div>
    );
  }
}

export default App;
