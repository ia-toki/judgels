import { Navigate } from '@tanstack/react-router';

export default function SettingsIndexPage() {
  return <Navigate to="/admin/settings/general" replace />;
}
