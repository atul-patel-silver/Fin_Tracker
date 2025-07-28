// src/pages/LoginPage.tsx
import React from 'react';
import LoginForm from '../../components/auth/LoginForm/LoginForm';

const LoginPage: React.FC = () => {
  return (
    <div className="page-wrapper"> {/* You can add a wrapper for consistent page styling */}
      <LoginForm />
    </div>
  );
};

export default LoginPage;