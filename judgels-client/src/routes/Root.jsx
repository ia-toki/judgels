import { HeadContent, Outlet } from '@tanstack/react-router';

import { GAListener } from '../components/GAListener/GAListener';

export default function Root() {
  return (
    <>
      <HeadContent />
      <GAListener />
      <Outlet />
    </>
  );
}
