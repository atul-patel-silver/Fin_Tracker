import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import styles from "./CallBack.module.css";

const Callback: React.FC = () => {
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const code = params.get("code");
    console.log("OAuth Code:", code); // ✅ MUST see a real code

    if (!code) {
      navigate("/login");
      return;
    }

    axios
      .post(
        `${process.env.REACT_APP_API_GATEWAY_SERVICE_URL}/callback`,
        { code },
        { withCredentials: true }
      )
      .then((response) => {
        console.log("Callback Response:", response);
        // localStorage.setItem("auth_token_temp", response.data.access_token);
        // navigate("/dashboard");
      })
      .catch((error) => {
        console.error(
          "Callback Error:",
          // error.response?.data || error.message
        );
        //navigate("/login?error=oauth");
      });

  }, [navigate]);

  return (
    <div className={styles.callbackPage}>
      {loading && (
        <div className={styles.spinnerContainer}>
          <div className={styles.spinner}></div>
          <p>Signing you in, please wait...</p>
        </div>
      )}
    </div>
  );
};

export default Callback;
