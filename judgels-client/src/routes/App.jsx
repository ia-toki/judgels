import { PortalProvider } from '@blueprintjs/core';
import { useSuspenseQuery } from '@tanstack/react-query';
import { Outlet } from '@tanstack/react-router';
import classNames from 'classnames';
import { useEffect } from 'react';

import Announcements from '../components/Announcements/Announcements';
import { AppContent } from '../components/AppContent/AppContent';
import { Footer } from '../components/Footer/Footer';
import Header from '../components/Header/Header';
import { setGAUser } from '../ga';
import { userWebConfigQueryOptions } from '../modules/queries/userWeb';
import { useSession } from '../modules/session';
import { useWebPrefs } from '../modules/webPrefs';
import { getHomeRoute, getVisibleAppRoutes } from './AppRoutes';

export default function App() {
  const { isDarkMode } = useWebPrefs();
  const { user } = useSession();
  const userJid = user?.jid;
  const {
    data: { role },
  } = useSuspenseQuery(userWebConfigQueryOptions());

  useEffect(() => {
    setGAUser(userJid);
  }, []);

  const visibleAppRoutes = getVisibleAppRoutes(role);
  const homeRoute = getHomeRoute();

  return (
    <div className={classNames({ 'bp6-light': !isDarkMode, 'bp6-dark': isDarkMode })}>
      <Announcements />
      <Header items={visibleAppRoutes} homeRoute={homeRoute} />
      <AppContent>
        <PortalProvider portalClassName={isDarkMode ? 'bp6-dark' : 'bp6-light'}>
          <Outlet />
        </PortalProvider>
        <Footer />
      </AppContent>
    </div>
  );
}
