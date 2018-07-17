import * as React from 'react';
import { RouteComponentProps, withRouter } from 'react-router';
import { connect } from 'react-redux';

import { breadcrumbsActions as injectedBreadcrumbsActions } from '../../../../../../modules/breadcrumbs/breadcrumbsActions';

export interface ContestsDataRouteProps extends RouteComponentProps<{ contestJid: string }> {
  onPushBreadcrumb: (link: string, title: string) => void;
  onPopBreadcrumb: (link: string) => void;
}

class ContestsDataRoute extends React.Component<ContestsDataRouteProps> {
  componentDidMount() {
    this.props.onPushBreadcrumb(this.props.match.url, 'Contests');
  }

  componentWillUnmount() {
    this.props.onPopBreadcrumb(this.props.match.url);
  }

  render() {
    return null;
  }
}

export function createContestsDataRoute(breadcrumbsActions) {
  const mapDispatchToProps = {
    onPushBreadcrumb: breadcrumbsActions.push,
    onPopBreadcrumb: breadcrumbsActions.pop,
  };

  return withRouter<any>(connect(undefined, mapDispatchToProps)(ContestsDataRoute));
}

export default createContestsDataRoute(injectedBreadcrumbsActions);
