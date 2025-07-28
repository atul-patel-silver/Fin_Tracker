// src/pages/RegisterPage.tsx
import React from 'react';
import { RegisterForm } from '../../components/auth';
// import RegisterForm from '../components/auth/RegisterForm/RegisterForm';

const RegisterPage: React.FC = () => {
  return (
    <div className="page-wrapper">
      <RegisterForm />
    </div>
  );
};

export default RegisterPage;