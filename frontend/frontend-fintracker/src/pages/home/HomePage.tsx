// src/pages/HomePage.tsx
import React from 'react';
import styles from './HomePage.module.css';

const HomePage: React.FC = () => {
  // Get isAuthenticated and user from Redux store

  return (
    <div className={`${styles.homePage} container`}>
      dashboard
    </div>
  );
};

export default HomePage;