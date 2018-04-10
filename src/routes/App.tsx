import * as React from 'react';
import { IntlProvider } from 'react-intl';
import { connect } from 'react-redux';
import { Route, Switch, withRouter } from 'react-router';
import DocumentTitle from 'react-document-title';

import { getAppRoutes, getHomeRoute } from './AppRoutes';
import Header from '../components/Header/Header';
import LabsRoutes from './labs/LabsRoutes';
import LegacyJophielRoutes from './legacyJophiel/LegacyJophielRoutes';
import { AppContent } from '../components/AppContent/AppContent';
import Menubar from '../components/Menubar/Menubar';
import Breadcrumbs from '../components/Breadcrumbs/Breadcrumbs';
import { Footer } from '../components/Footer/Footer';
import { JophielRole } from '../modules/api/jophiel/my';
import { AppState } from '../modules/store';
import { selectDocumentTitle } from '../modules/breadcrumbs/breadcrumbsSelectors';
import { selectRole } from './jophiel/modules/roleSelectors';
import { roleActions as injectedRoleActions } from './jophiel/modules/roleActions';
import { webConfigActions as injectedWebConfigActions } from './jophiel/modules/webConfigActions';

interface AppProps {
  title: string;
  role: JophielRole;
  onGetRole: () => void;
  onGetWebConfig: () => void;
}

class App extends React.Component<AppProps> {
  componentDidMount() {
    this.props.onGetRole();
    this.props.onGetWebConfig();
  }

  render() {
    const appRoutes = getAppRoutes(this.props.role);
    const homeRoute = getHomeRoute();

    return (
      <DocumentTitle title={this.props.title}>
        <IntlProvider locale={navigator.language}>
          <div>
            <Header />
            <Menubar items={appRoutes} homeRoute={homeRoute} />
            <AppContent>
              <Breadcrumbs />
              <Switch>
                {appRoutes.map(item => <Route key={item.id} {...item.route} />)}
                <Route path="/labs" component={LabsRoutes} />
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

export function createApp(roleActions, webConfigActions) {
  const mapStateToProps = (state: AppState) => ({
    title: selectDocumentTitle(state),
    role: selectRole(state),
  });
  const mapDispatchToProps = {
    onGetRole: roleActions.get,
    onGetWebConfig: webConfigActions.get,
  };
  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(App));
}

export default createApp(injectedRoleActions, injectedWebConfigActions);
