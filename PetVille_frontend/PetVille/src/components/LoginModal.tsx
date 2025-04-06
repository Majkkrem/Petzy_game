import React from 'react';
import { Modal, Button } from 'react-bootstrap';
import RegisterModal from './RegisterModal';
import { useAuth } from './AuthProvider';

interface LoginModalProps {
  show: boolean;
  onHide: () => void;
  onShow: () => void;
}

const LoginModal: React.FC<LoginModalProps> = ({ show, onHide, onShow }) => {
  const [showRegistrationModal, setShowRegistrationModal] = React.useState(false);
  const [registrationSuccess, setRegistrationSuccess] = React.useState(false);
  const [email, setEmail] = React.useState('');
  const [password, setPassword] = React.useState('');
  const { login } = useAuth();

  const handleRegistrationClick = () => {
    setShowRegistrationModal(true);
    onHide();
  };

  const handleCloseRegistrationModal = () => {
    setShowRegistrationModal(false);
    setRegistrationSuccess(false); 
  };

  const handleRegistrationSuccess = (registeredEmail: string) => {
    setEmail(registeredEmail);
    setRegistrationSuccess(true);
    setShowRegistrationModal(false);
    onShow();
  };

  const handleLogin = async () => {
    try {
      console.log('Logging in:', email);
      await login(email, password);
      onHide();
    } catch (error) {
      console.error('Login failed:', error);
    }
  };


  React.useEffect(() => {
    if (registrationSuccess) {
      const timer = setTimeout(() => {
        if (typeof onHide === 'function') {
          onHide(); 
        }
      }, 300);
      
      return () => clearTimeout(timer);
    }
  }, [registrationSuccess, onHide]);

  return (
    <>
      <Modal show={show} onHide={onHide}>
        <Modal.Header closeButton>
          <Modal.Title>Login</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <div className="mb-3">
            <label htmlFor="email" className="form-label">Email:</label>
            <input 
              type="email" 
              id="loginEmail"
              className="form-control" 
              placeholder="Enter email" 
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>
          <div className="mb-3">
            <label htmlFor="password" className="form-label">Password:</label>
            <input 
              type="password" 
              id="loginPassword"
              className="form-control" 
              placeholder="Password" 
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
          <div>
            <p>
              Don't have an account?{' '}
              <a 
                href='#' 
                onClick={(e) => { 
                  e.preventDefault(); 
                  handleRegistrationClick();
                }}
                style={{ cursor: 'pointer' }}
              >
                Sign up
              </a>
            </p>
          </div>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={onHide}>
            Close
          </Button>
          <Button variant="primary" onClick={handleLogin}>
            Login
          </Button>
        </Modal.Footer>
      </Modal>

      <RegisterModal
        show={showRegistrationModal} 
        onHide={handleCloseRegistrationModal}
        onRegistrationSuccess={handleRegistrationSuccess}
      />
    </>
  );
};

export default LoginModal;