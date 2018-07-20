import * as React from 'react';
import { RouteComponentProps, withRouter } from 'react-router';
import { connect } from 'react-redux';

import { publicProfileActions as injectedPublicProfileActions } from '../../../../modules/publicProfileActions';
import { breadcrumbsActions as injectedBreadcrumbsActions } from '../../../../../../modules/breadcrumbs/breadcrumbsActions';

export interface SingleProfileDataRouteProps extends RouteComponentProps<{ username: string }> {
  onGetPublicProfile: (username: string) => void;
  onClearPublicProfile: () => void;
  onPushBreadcrumb: (link: string, title: string) => void;
  onPopBreadcrumb: (link: string) => void;
}

class SingleProfileDataRoute extends React.Component<SingleProfileDataRouteProps> {
  componentDidMount() {
    this.props.onGetPublicProfile(this.props.match.params.username);
    this.props.onPushBreadcrumb(this.props.match.url, this.props.match.params.username);
  }

  componentDidUpdate(prevProps: SingleProfileDataRouteProps) {
    if (this.props.match.params.username !== prevProps.match.params.username) {
      this.props.onGetPublicProfile(this.props.match.params.username);
      this.props.onPopBreadcrumb(this.props.match.url.replace(/\/+$/, ''));
      this.props.onPushBreadcrumb(this.props.match.url, this.props.match.params.username);
    }
  }

  componentWillUnmount() {
    this.props.onClearPublicProfile();
    this.props.onPopBreadcrumb(this.props.match.url.replace(/\/+$/, ''));
  }

  render() {
    return null;
  }
}

export function createSingleProfileDataRoute(publicProfileActions, breadcrumbsActions) {
  const mapDispatchToProps = {
    onGetPublicProfile: publicProfileActions.getPublicProfile,
    onClearPublicProfile: publicProfileActions.clearPublicProfile,
    onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
    onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
  };

  return withRouter<any>(connect(undefined, mapDispatchToProps)(SingleProfileDataRoute));
}

export default createSingleProfileDataRoute(injectedPublicProfileActions, injectedBreadcrumbsActions);
