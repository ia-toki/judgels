import { PortalProvider } from '@blueprintjs/core';
import classNames from 'classnames';
import { PureComponent } from 'react';
import DocumentTitle from 'react-document-title';
import { connect } from 'react-redux';
import { Route, Switch } from 'react-router';

import Announcements from '../components/Announcements/Announcements';
import { AppContent } from '../components/AppContent/AppContent';
import { Footer } from '../components/Footer/Footer';
import Header from '../components/Header/Header';
import { setGAUser } from '../ga';
import { selectDocumentTitle } from '../modules/breadcrumbs/breadcrumbsSelectors';
import { selectMaybeUserJid } from '../modules/session/sessionSelectors';
import { selectIsDarkMode } from '../modules/webPrefs/webPrefsSelectors';
import { getHomeRoute, getVisibleAppRoutes, preloadRoutes } from './AppRoutes';
import { selectRole } from './jophiel/modules/userWebSelectors';

import * as userWebActions from './jophiel/modules/userWebActions';

class App extends PureComponent {
  componentDidMount() {
    this.props.onGetUserWebConfig();
    preloadRoutes();
    setGAUser(this.props.userJid);
  }

  render() {
    const { isDarkMode, title, role } = this.props;

    const visibleAppRoutes = getVisibleAppRoutes(role);
    const homeRoute = getHomeRoute();

    return (
      <DocumentTitle title={title}>
        <div className={classNames({ 'bp5-light': !isDarkMode, 'bp5-dark': isDarkMode })}>
          <Announcements />
          <Header items={visibleAppRoutes} homeRoute={homeRoute} />
          <AppContent>
            <PortalProvider portalClassName={isDarkMode ? 'bp5-dark' : 'bp5-light'}>
              <Switch>
                {visibleAppRoutes.map(item => (
                  <Route key={item.id} {...item.route} />
                ))}
                <Route {...homeRoute.route} />
              </Switch>
            </PortalProvider>
            <Footer />
          </AppContent>
        </div>
      </DocumentTitle>
    );
  }
}

const mapStateToProps = state => ({
  isDarkMode: selectIsDarkMode(state),
  userJid: selectMaybeUserJid(state),
  title: selectDocumentTitle(state),
  role: selectRole(state),
});
const mapDispatchToProps = {
  onGetUserWebConfig: userWebActions.getWebConfig,
};
export default connect(mapStateToProps, mapDispatchToProps)(App);
