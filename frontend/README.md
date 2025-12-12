# Hospital Management System - Frontend

React + TypeScript frontend for the Hospital Database Backend application.

## Features

- **Authentication**: JWT-based login system with role-based access control
- **Patients Management**: Full CRUD operations for patient records
- **Doctors Management**: Full CRUD operations for doctor records
- **Appointments Management**: Full CRUD operations for appointments
- **Dashboard**: Overview with statistics and quick actions
- **Responsive Design**: Modern UI built with Tailwind CSS

## Prerequisites

- Node.js 18+ and npm
- Backend server running on `http://localhost:8080`

## Installation

```bash
npm install
```

## Configuration

Create a `.env` file in the frontend directory (optional):

```env
VITE_API_BASE_URL=http://localhost:8080
```

If not set, it defaults to `http://localhost:8080`.

## Running the Application

```bash
npm run dev
```

The application will start on `http://localhost:5173` (or the next available port).

## Default Credentials

- Username: `admin`
- Password: `admin`

## Project Structure

```
frontend/
├── src/
│   ├── components/     # Reusable components
│   │   ├── Layout/     # Layout and Navbar
│   │   └── common/      # Common components (Loading, Error, etc.)
│   ├── pages/          # Page components
│   │   ├── Patients/   # Patient CRUD pages
│   │   ├── Doctors/    # Doctor CRUD pages
│   │   └── Appointments/ # Appointment CRUD pages
│   ├── services/       # API service layer
│   ├── context/        # React Context (Auth)
│   ├── types/          # TypeScript interfaces
│   └── utils/          # Utility functions
├── package.json
└── vite.config.ts
```

## Building for Production

```bash
npm run build
```

The built files will be in the `dist/` directory.

## Testing

The frontend is set up for e2e testing and stress testing. Make sure the backend is running before running tests.
