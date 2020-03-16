import * as React from 'react';
import { IntlProvider } from 'react-intl';
import { connect } from 'react-redux';
import { Route, Switch } from 'react-router';
import DocumentTitle from 'react-document-title';

import { setGAUser } from '../ga';
import Header from '../components/Header/Header';
import { AppContent } from '../components/AppContent/AppContent';
import Menubar from '../components/Menubar/Menubar';
import Breadcrumbs from '../components/Breadcrumbs/Breadcrumbs';
import { Footer } from '../components/Footer/Footer';
import { UserRole } from '../modules/api/jophiel/role';
import { AppState } from '../modules/store';
import { selectDocumentTitle } from '../modules/breadcrumbs/breadcrumbsSelectors';
import { selectMaybeUserJid } from '../modules/session/sessionSelectors';

import { getAppRoutes, getHomeRoute, getVisibleAppRoutes, preloadRoutes } from './AppRoutes';
import LegacyJophielRoutes from './legacyJophiel/LegacyJophielRoutes';
import { selectRole } from './jophiel/modules/userWebSelectors';
import * as userWebActions from './jophiel/modules/userWebActions';

interface AppProps {
  title: string;
  userJid?: string;
  role: UserRole;
  onGetUserWebConfig: () => void;
}

class App extends React.PureComponent<AppProps> {
  componentDidMount() {
    this.props.onGetUserWebConfig();
    preloadRoutes();
    setGAUser(this.props.userJid);
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
              <Footer />
            </AppContent>
          </div>
        </IntlProvider>
      </DocumentTitle>
    );
  }
}

const mapStateToProps = (state: AppState) => ({
  userJid: selectMaybeUserJid(state),
  title: selectDocumentTitle(state),
  role: selectRole(state),
});
const mapDispatchToProps = {
  onGetUserWebConfig: userWebActions.getWebConfig,
};
export default connect(mapStateToProps, mapDispatchToProps)(App);
