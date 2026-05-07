import { useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const GoogleSignInButton = () => {
  const buttonRef = useRef(null);
  const initialized = useRef(false);

  const navigate = useNavigate();
  const { handleOAuthLogin } = useAuth();

  useEffect(() => {
    if (!window.google || !buttonRef.current || initialized.current) return;

    initialized.current = true;

    window.google.accounts.id.initialize({
      client_id:
        '562223260258-kj4t74lp7qfhd63j9i3det63l2vomdtb.apps.googleusercontent.com',
      callback: async ({ credential }) => {
        const result = await handleOAuthLogin(credential);

        if (result?.success) {
          navigate('/');
        }
      },
    });

    window.google.accounts.id.renderButton(buttonRef.current, {
      type: 'standard',
      theme: 'outline',
      size: 'large',
      text: 'continue_with',
    });
  }, [handleOAuthLogin, navigate]);

  return <div ref={buttonRef} />;
};

export default GoogleSignInButton;