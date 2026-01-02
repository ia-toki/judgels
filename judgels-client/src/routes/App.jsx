import { PortalProvider } from '@blueprintjs/core';
import { Outlet } from '@tanstack/react-router';
import classNames from 'classnames';
import { useEffect } from 'react';
import DocumentTitle from 'react-document-title';
import { useDispatch, useSelector } from 'react-redux';

import Announcements from '../components/Announcements/Announcements';
import { AppContent } from '../components/AppContent/AppContent';
import { Footer } from '../components/Footer/Footer';
import Header from '../components/Header/Header';
import { setGAUser } from '../ga';
import { selectDocumentTitle } from '../modules/breadcrumbs/breadcrumbsSelectors';
import { selectMaybeUserJid } from '../modules/session/sessionSelectors';
import { selectIsDarkMode } from '../modules/webPrefs/webPrefsSelectors';
import { getHomeRoute, getVisibleAppRoutes } from './AppRoutes';
import { selectRole } from './jophiel/modules/userWebSelectors';

import * as userWebActions from './jophiel/modules/userWebActions';

export default function App() {
  const dispatch = useDispatch();
  const isDarkMode = useSelector(selectIsDarkMode);
  const userJid = useSelector(selectMaybeUserJid);
  const title = useSelector(selectDocumentTitle);
  const role = useSelector(selectRole);

  useEffect(() => {
    dispatch(userWebActions.getWebConfig());
    setGAUser(userJid);
  }, []);

  const visibleAppRoutes = getVisibleAppRoutes(role);
  const homeRoute = getHomeRoute();

  return (
    <DocumentTitle title={title}>
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
    </DocumentTitle>
  );
}
