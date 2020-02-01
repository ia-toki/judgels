import * as React from 'react';
import * as ReactGA from 'react-ga';
import { IntlProvider } from 'react-intl';
import { connect } from 'react-redux';
import { Route, Switch, withRouter } from 'react-router';
import DocumentTitle from 'react-document-title';

import { APP_CONFIG } from '../conf';
import Header from '../components/Header/Header';
import { AppContent } from '../components/AppContent/AppContent';
import Menubar from '../components/Menubar/Menubar';
import Breadcrumbs from '../components/Breadcrumbs/Breadcrumbs';
import { Footer } from '../components/Footer/Footer';
import { JophielRole } from '../modules/api/jophiel/role';
import { AppState } from '../modules/store';
import { selectDocumentTitle } from '../modules/breadcrumbs/breadcrumbsSelectors';
import { selectMaybeUserJid } from '../modules/session/sessionSelectors';

import { getAppRoutes, getHomeRoute, getVisibleAppRoutes, preloadRoutes } from './AppRoutes';
import LegacyJophielRoutes from './legacyJophiel/LegacyJophielRoutes';
import LegacyCompetitionRoute from './legacyUriel/LegacyCompetitionRoute';
import { selectRole } from './jophiel/modules/userWebSelectors';
import { userWebActions as injectedUserWebActions } from './jophiel/modules/userWebActions';

interface AppProps {
  title: string;
  userJid?: string;
  role: JophielRole;
  onGetUserWebConfig: () => void;
}

class App extends React.PureComponent<AppProps> {
  componentDidMount() {
    this.props.onGetUserWebConfig();
    preloadRoutes();

    if (APP_CONFIG.googleAnalytics) {
      if (this.props.userJid) {
        ReactGA.set({ userId: this.props.userJid });
      }
    }
  }

  render() {
    const appRoutes = getAppRoutes();
    const visibleAppRoutes = getVisibleAppRoutes(this.props.role);
    const homeRoute = getHomeRoute();

    return (
      <DocumentTitle title={this.props.title}>
        <IntlProvider locale={navigator.language}>
          <div>
            <Header />
            <Menubar items={visibleAppRoutes} homeRoute={homeRoute} />
            <AppContent>
              <Breadcrumbs />
              <Switch>
                {appRoutes.map(item => (
                  <Route key={item.id} {...item.route} />
                ))}
                <Route {...homeRoute.route} />
              </Switch>
              <Route component={LegacyJophielRoutes} />
              <Route path="/competition/contests/:contestSlug" component={LegacyCompetitionRoute} />
              <Footer />
            </AppContent>
          </div>
        </IntlProvider>
      </DocumentTitle>
    );
  }
}

export function createApp(userWebActions) {
  const mapStateToProps = (state: AppState) => ({
    userJid: selectMaybeUserJid(state),
    title: selectDocumentTitle(state),
    role: selectRole(state),
  });
  const mapDispatchToProps = {
    onGetUserWebConfig: userWebActions.getWebConfig,
  };
  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(App));
}

export default createApp(injectedUserWebActions);
