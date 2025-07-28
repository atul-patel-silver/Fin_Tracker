import React from 'react';
import { Link } from 'react-router-dom';
import styles from './Navbar.module.css';
import Button from '../../common/Button/Button';
import { useTheme } from '../../../contexts';

const Navbar: React.FC = () => {
  const isLoggedIn = false; // Replace with actual auth state
  const userName = 'John';  // Replace with actual user name
  const { theme, toggleTheme } = useTheme();

  const handleLogout = () => {
    console.log('Logout logic here');
  };

  return (
    <nav className={styles.navbar}>
      <div className={styles.container}>
        <Link to="/" className={styles.logo}>
          MyApp
        </Link>
        <div className={styles.navLinks}>
          <Link to="/" className={styles.navLink}>Home</Link>

          {isLoggedIn ? (
            <>
              <span className={styles.welcomeText}>Hello, {userName}!</span>
              <Button onClick={handleLogout} variant="secondary" size="small">
                Logout
              </Button>
            </>
          ) : (
            <>
              <Link to="/login" className={styles.navLink}>Login</Link>
              <Link to="/register" className={styles.navLink}>Register</Link>
            </>
          )}

          <button onClick={toggleTheme} className={styles.themeToggle}>
            {theme === 'light' ? '🌙 Dark Mode' : '☀️ Light Mode'}
          </button>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
