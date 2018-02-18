import * as React from 'react';
import { connect } from 'react-redux';
import { Route, Switch, withRouter } from 'react-router';
import DocumentTitle from 'react-document-title';

import Header from '../components/Header/Header';
import LabsRoutes from './labs/LabsRoutes';
import LegacyJophielRoutes from './legacyJophiel/LegacyJophielRoutes';
import JophielRoutes from './jophiel/JophielRoutes';
import CompetitionRoutes from './uriel/competition/routes/CompetitionRoutes';
import { AppContent } from '../components/AppContent/AppContent';
import Menubar from '../components/Menubar/Menubar';
import Breadcrumbs from '../components/Breadcrumbs/Breadcrumbs';
import { Footer } from '../components/Footer/Footer';
import { webConfigActions as injectedWebConfigActions } from './jophiel/modules/webConfigActions';
import { AppState } from '../modules/store';
import { selectDocumentTitle } from '../modules/breadcrumbs/breadcrumbsSelectors';

interface AppProps {
  title: string;
  onGetWebConfig: () => Promise<void>;
}

class App extends React.Component<AppProps> {
  async componentDidMount() {
    await this.props.onGetWebConfig();
  }

  render() {
    const appRoutes = [];

    const homeRoute = {
      id: 'home',
      title: 'Home',
      route: {
        component: JophielRoutes,
      },
    };

    return (
      <DocumentTitle title={this.props.title}>
        <div>
          <Header />
          <Menubar items={appRoutes} homeRoute={homeRoute} />
          <AppContent>
            <Breadcrumbs />
            <Switch>
              <Route path="/labs" component={LabsRoutes} />
              <Route path="/competition" component={CompetitionRoutes} />
              <Route {...homeRoute.route} />
            </Switch>
            <Route component={LegacyJophielRoutes} />
            <Footer />
          </AppContent>
        </div>
      </DocumentTitle>
    );
  }
}

export function createApp(webConfigActions) {
  const mapStateToProps = (state: AppState) => ({
    title: selectDocumentTitle(state),
  });
  const mapDispatchToProps = dispatch => ({
    onGetWebConfig: () => dispatch(webConfigActions.get()),
  });
  return withRouter(connect(mapStateToProps, mapDispatchToProps)(App));
}

export default createApp(injectedWebConfigActions);
