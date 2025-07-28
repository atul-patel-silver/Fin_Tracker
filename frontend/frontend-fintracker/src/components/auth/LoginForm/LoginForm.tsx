import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import InputField from '../../common/InputField/InputField';
import Button from '../../common/Button/Button';
import { LoginDto } from '../../../interfaces/auth';
import { useFormValidation } from '../../../hooks/useFormValidation';
import { validateLogin } from '../../../utils/validation';
import styles from './LoginForm.module.css';

interface LoginFormProps {
  onLoginSuccess?: () => void;
}

const LoginForm: React.FC<LoginFormProps> = ({ onLoginSuccess }) => {
  const navigate = useNavigate();

  const {
    formData,
    errors,
    handleChange,
    handleSubmit,
    setGeneralError,
    generalError,
  } = useFormValidation<LoginDto>(
    { userName: '', password: '' },
    validateLogin
  );

  const handleLoginSubmit = async (data: LoginDto) => {
    try {
      // Example static auth logic
      if (data.userName === 'admin' && data.password === 'admin123') {
        onLoginSuccess?.();
        navigate('/dashboard');
      } else {
        setGeneralError('Invalid username or password');
      }
    } catch (error) {
      setGeneralError('Something went wrong. Please try again later.');
    }
  };

const handleGoogleLogin = () => {
  const baseUrl = process.env.REACT_APP_KEYCLOAK_BASE_URL;
  const realm = process.env.REACT_APP_KEYCLOAK_REALM;
  const clientId = process.env.REACT_APP_KEYCLOAK_CLIENT_ID;
  const redirectUri = process.env.REACT_APP_KEYCLOAK_REDIRECT_URI;
  const scope = process.env.REACT_APP_KEYCLOAK_SCOPE;
  // const idpHint = process.env.REACT_APP_KEYCLOAK_IDP_HINT;

  const authUrl =
    `${baseUrl}/realms/${realm}/protocol/openid-connect/auth` +
    `?client_id=${clientId}` +
    `&response_type=code` +
    `&scope=${scope}` +
    `&redirect_uri=${redirectUri}` +
    `&kc_idp_hint=google`;

  window.location.href = authUrl;
  };
  return (
    <form className={`${styles.loginForm} card`} onSubmit={(e) => handleSubmit(e, handleLoginSubmit)}>
      <h2>Login</h2>
      <InputField
        label="Username"
        type="text"
        id="userName"
        name="userName"
        value={formData.userName}
        onChange={handleChange}
        error={errors.userName}
        autoComplete="username"
      />
      <InputField
        label="Password"
        type="password"
        id="password"
        name="password"
        value={formData.password}
        onChange={handleChange}
        error={errors.password}
        autoComplete="current-password"
      />
      {generalError && <p className={styles.errorText}>{generalError}</p>}
      <Button type="submit">Login</Button>
      <div className={styles.orDivider}>OR</div>
      <div className={styles.socialLoginButtons}>
       <button onClick={handleGoogleLogin} className={styles.googleButton}>
        <img src="/google-icon.svg" alt="Google" />
        Continue with Google
      </button>
      </div>
      <p className={styles.registerLink}>
        Don't have an account? <Link to="/register">Register here</Link>
      </p>
    </form>
  );
};

export default LoginForm;
