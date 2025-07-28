import React from 'react';
import styles from './Footer.module.css';

const Footer: React.FC = () => {
  return (
    <footer className={styles.footer}>
      <div className={styles.container}>
        <div className={styles.section}>
          <h3 className={styles.title}>About</h3>
          <p className={styles.text}>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam convallis, urna at volutpat tincidunt, justo magna volutpat sapien.
          </p>
        </div>

        <div className={styles.section}>
          <h3 className={styles.title}>Information</h3>
          <p className={styles.text}>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis dignissim, augue at commodo commodo, ex turpis faucibus metus.
          </p>
        </div>

        <div className={styles.section}>
          <h3 className={styles.title}>Contact</h3>
          <p className={styles.text}>Lorem ipsum dolor sit amet, consectetur.</p>
          <p className={styles.text}>Phone: +91-9876543210</p>
        </div>
      </div>

      <div className={styles.bottomBar}>
        <p>&copy; {new Date().getFullYear()} MyApp. All rights reserved.</p>
      </div>
    </footer>
  );
};

export default Footer;
