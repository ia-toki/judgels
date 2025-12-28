import { Route, Routes } from 'react-router';

import GuestRoute from '../../components/GuestRoute/GuestRoute';
import UserRoute from '../../components/UserRoute/UserRoute';
import HomePage from '../home/HomePage/HomePage';
import JophielAccountRoutes from './JophielAccountRoutes';
import JophielProfilesRoutes from './JophielProfilesRoutes';
import ActivatePage from './activate/ActivatePage/ActivatePage';
import ForgotPasswordPage from './forgotPassword/ForgotPasswordPage/ForgotPasswordPage';
import LoginPage from './login/LoginPage/LoginPage';
import LogoutPage from './logout/LogoutPage/LogoutPage';
import NeedActivationPage from './needActivation/NeedActivationPage/NeedActivationPage';
import RegisterPage from './register/RegisterPage/RegisterPage';
import RegisteredPage from './registered/RegisteredPage/RegisteredPage';
import ResetPasswordPage from './resetPassword/ResetPasswordPage/ResetPasswordPage';

function JophielRoutes() {
  return (
    <div>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/registered" element={<RegisteredPage />} />
        <Route
          path="/login"
          element={
            <GuestRoute>
              <LoginPage />
            </GuestRoute>
          }
        />
        <Route
          path="/logout"
          element={
            <UserRoute>
              <LogoutPage />
            </UserRoute>
          }
        />
        <Route
          path="/register"
          element={
            <GuestRoute>
              <RegisterPage />
            </GuestRoute>
          }
        />
        <Route
          path="/activate/:emailCode"
          element={
            <GuestRoute>
              <ActivatePage />
            </GuestRoute>
          }
        />
        <Route
          path="/forgot-password"
          element={
            <GuestRoute>
              <ForgotPasswordPage />
            </GuestRoute>
          }
        />
        <Route
          path="/need-activation"
          element={
            <GuestRoute>
              <NeedActivationPage />
            </GuestRoute>
          }
        />
        <Route
          path="/reset-password/:emailCode"
          element={
            <GuestRoute>
              <ResetPasswordPage />
            </GuestRoute>
          }
        />
        <Route
          path="/account/*"
          element={
            <UserRoute>
              <JophielAccountRoutes />
            </UserRoute>
          }
        />
        <Route path="/profiles/*" element={<JophielProfilesRoutes />} />
      </Routes>
    </div>
  );
}

export default JophielRoutes;
