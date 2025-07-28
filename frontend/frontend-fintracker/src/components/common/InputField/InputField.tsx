// src/components/common/InputField/InputField.tsx
import React from 'react';
import styles from './InputField.module.css';

interface InputFieldProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
}

const InputField: React.FC<InputFieldProps> = ({ label, error, className, ...props }) => {
  return (
    <div className={`${styles.inputGroup} ${className || ''}`}>
      {label && <label htmlFor={props.id} className={styles.label}>{label}</label>}
      <input
        className={`${styles.input} ${error ? styles.inputError : ''}`}
        {...props}
      />
      {error && <p className={styles.errorText}>{error}</p>}
    </div>
  );
};

export default InputField;