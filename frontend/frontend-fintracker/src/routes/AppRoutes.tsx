// src/routes/AppRoutes.tsx
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import LoginPage from '../pages/auth/LoginPage';
import HomePage from '../pages/home/HomePage';

import RegisterPage from '../pages/auth/RegisterPage';
import NotFoundPage from '../pages/error/NotFoundPage';
import Navbar from '../components/layout/Navbar/Navbar';
import Footer from '../components/layout/Footer/Footer';
import { Callback } from '../components/auth';

// A simple wrapper for private routes
// const PrivateRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {

//   // if (!isAuthenticated) {
//   //   // Redirect them to the /login page, but save the current location they were
//   //   // trying to go to when they were redirected. This allows us to send them
//   //   // along to that page after they login, which is a nicer user experience
//   //   // than dropping them off on the home page.
//   //   return <Navigate to="/login" state={{ from: location }} replace />;
//   // }

//   // return <>{children}</>;
// };


const AppRoutes: React.FC = () => {
  // We use useAppSelector here to control redirect logic in the routes
  // based on Redux state.
  // const isAuthenticated = useAppSelector((state) => state.auth.isAuthenticated);

  return (
    <Router>
        <Navbar />
        <main style={{ flexGrow: 1, display: 'flex', flexDirection: 'column' }}>
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route
              path="/login"
              element={<LoginPage/>}
            />
            <Route
              path="/register"
              element={<RegisterPage />}
            />
            <Route
              path="/callback"
              element={<Callback />}
            />
            {/* Example of a private route - uncomment and create DashboardPage if needed */}
            {/* <Route
              path="/dashboard"
              element={
                <PrivateRoute>
                  <DashboardPage />
                </PrivateRoute>
              }
            /> */}
            <Route path="*" element={<NotFoundPage />} />
          </Routes>
        </main>
        <Footer />
    </Router>
  );
};

export default AppRoutes;