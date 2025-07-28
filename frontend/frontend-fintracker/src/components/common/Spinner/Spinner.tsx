import React from 'react'; // <--- Ensure React is imported
import styles from './Spinner.module.css';

const Spinner: React.FC = () => {
  return (
    <div className={styles.spinnerContainer}>
      <div className={styles.spinner}></div>
    </div>
  );
};

export default Spinner; // <--- Ensure there's a default export