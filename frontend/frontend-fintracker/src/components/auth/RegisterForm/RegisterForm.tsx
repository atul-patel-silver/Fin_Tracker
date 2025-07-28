import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import InputField from '../../common/InputField/InputField';
import Button from '../../common/Button/Button';
import { UserDTO } from '../../../interfaces/auth';
import { useFormValidation } from '../../../hooks/useFormValidation';
import { validateRegister } from '../../../utils/validation';
import styles from './RegisterForm.module.css';

interface ExtendedUserDTO extends UserDTO {
  confirmPassword?: string;
}

interface RegisterFormProps {
  onRegisterSuccess?: () => void;
}

const RegisterForm: React.FC<RegisterFormProps> = ({ onRegisterSuccess }) => {
  const navigate = useNavigate();

  const {
    formData,
    errors,
    handleChange,
    handleSubmit,
    setGeneralError,
    generalError,
  } = useFormValidation<ExtendedUserDTO>(
    {
      firstName: '',
      middleName: '',
      lastName: '',
      emailId: '',
      mobileNumber: '',
      userName: '',
      password: '',
      confirmPassword: '',
    },
    validateRegister
  );

  const handleRegisterSubmit = async (data: ExtendedUserDTO) => {
    if (data.password !== data.confirmPassword) {
      setGeneralError('Passwords do not match.');
      return;
    }

    try {
      console.log('Register Data:', data);

      // Example API call:
      // await api.post('/register', data);

      onRegisterSuccess?.();
      navigate('/login');
    } catch (error) {
      setGeneralError('Registration failed. Please try again.');
    }
  };

  return (
    <form className={`${styles.registerForm} card`} onSubmit={(e) => handleSubmit(e, handleRegisterSubmit)}>
  <h2>Create Account</h2>

  <div className={styles.row}>
    <InputField label="First Name" type="text" name="firstName" value={formData.firstName} onChange={handleChange} error={errors.firstName} />
    <InputField label="Last Name" type="text" name="lastName" value={formData.lastName} onChange={handleChange} error={errors.lastName} />
  </div>

  <InputField label="Middle Name" type="text" name="middleName" value={formData.middleName} onChange={handleChange} error={errors.middleName} />

  <div className={styles.row}>
    <InputField label="Email" type="email" name="emailId" value={formData.emailId} onChange={handleChange} error={errors.emailId} />
    <InputField label="Mobile Number" type="tel" name="mobileNumber" value={formData.mobileNumber} onChange={handleChange} error={errors.mobileNumber} />
  </div>

  <InputField label="Username" type="text" name="userName" value={formData.userName} onChange={handleChange} error={errors.userName} />
  <InputField label="Password" type="password" name="password" value={formData.password} onChange={handleChange} error={errors.password} />
  <InputField label="Confirm Password" type="password" name="confirmPassword" value={formData.confirmPassword} onChange={handleChange} error={errors.confirmPassword} />

  {generalError && <p className={styles.errorText}>{generalError}</p>}

  <Button type="submit">Register</Button>
  <p className={styles.loginLink}>
  Already have an account? <Link to="/login">Login here</Link>
</p>
</form>

  );
};

export default RegisterForm;
