import * as React from 'react';
import { connect } from 'react-redux';
import { Route, Switch, withRouter } from 'react-router';
import DocumentTitle from 'react-document-title';

import Competition from './competition/Competition';
import HeaderContainer from '../components/Header/Header';
import LabsContainer from './labs/Labs';
import LegacyJophielContainer from './legacyJophiel/LegacyJophiel';
import JophielContainer from './jophiel/Jophiel';
import { AppContent } from '../components/AppContent/AppContent';
import Menubar from '../components/Menubar/Menubar';
import BreadcrumbsContainer from '../components/Breadcrumbs/Breadcrumbs';
import { Footer } from '../components/Footer/Footer';
import { webConfigActions as injectedWebConfigActions } from './jophiel/modules/webConfigActions';
import { AppState } from '../modules/store';
import { selectDocumentTitle } from '../modules/breadcrumbs/breadcrumbsSelectors';

interface AppContainerConnectedProps {
  title: string;
  onGetWebConfig: () => Promise<void>;
}

const routeDefs = [
  {
    id: 'competition',
    title: 'Competition',
    route: {
      path: '/competition',
      component: Competition,
    },
  },
];

const homeRoute = {
  id: 'home',
  title: 'Home',
  route: {
    component: JophielContainer,
  },
};

class AppContainer extends React.Component<AppContainerConnectedProps> {
  async componentDidMount() {
    await this.props.onGetWebConfig();
  }

  render() {
    return (
      <DocumentTitle title={this.props.title}>
        <div>
          <HeaderContainer />
          <Menubar items={routeDefs} homeRoute={homeRoute} />
          <AppContent>
            <BreadcrumbsContainer />
            <Switch>
              {routeDefs.map(item => <Route key={item.id} {...item.route} />)}{' '}
              <Route key="labs" path="/labs" component={LabsContainer} />
              <Route key={homeRoute.id} {...homeRoute.route} />
            </Switch>
            <Route component={LegacyJophielContainer} />
            <Footer />
          </AppContent>
        </div>
      </DocumentTitle>
    );
  }
}

export function createAppContainer(webConfigActions) {
  const mapStateToProps = (state: AppState) => ({
    title: selectDocumentTitle(state),
  });
  const mapDispatchToProps = dispatch => ({
    onGetWebConfig: () => dispatch(webConfigActions.get()),
  });
  return withRouter(connect(mapStateToProps, mapDispatchToProps)(AppContainer));
}

export default createAppContainer(injectedWebConfigActions);
