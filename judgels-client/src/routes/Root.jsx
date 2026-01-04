import { HeadContent, Outlet } from '@tanstack/react-router';

import { GAListener } from '../components/GAListener/GAListener';
import { NavigationSetter } from '../modules/navigation/NavigationSetter';

export default function Root() {
  return (
    <>
      <HeadContent />
      <NavigationSetter />
      <GAListener />
      <Outlet />
    </>
  );
}
