// src/components/common/Button/Button.tsx
import React from 'react';
import styles from './Button.module.css';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'danger';
  size?: 'small' | 'medium' | 'large';
  children: React.ReactNode;
}

const Button: React.FC<ButtonProps> = ({
  variant = 'primary',
  size = 'medium',
  children,
  className,
  ...props
}) => {
  const buttonClass = `${styles.button} ${styles[variant]} ${styles[size]} ${className || ''}`;
  return (
    <button className={buttonClass} {...props}>
      {children}
    </button>
  );
};

export default Button;