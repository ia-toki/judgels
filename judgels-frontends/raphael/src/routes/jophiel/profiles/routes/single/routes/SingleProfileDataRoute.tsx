import * as React from 'react';
import { RouteComponentProps, withRouter } from 'react-router';
import { connect } from 'react-redux';

import { profileActions as injectedProfileActions } from '../../../../modules/profileActions';
import { breadcrumbsActions as injectedBreadcrumbsActions } from '../../../../../../modules/breadcrumbs/breadcrumbsActions';

export interface SingleProfileDataRouteProps extends RouteComponentProps<{ username: string }> {
  onGetUserJid: (username: string) => void;
  onClearUserJid: () => void;
  onPushBreadcrumb: (link: string, title: string) => void;
  onPopBreadcrumb: (link: string) => void;
}

class SingleProfileDataRoute extends React.Component<SingleProfileDataRouteProps> {
  componentDidMount() {
    this.props.onGetUserJid(this.props.match.params.username);
    this.props.onPushBreadcrumb(this.props.match.url, this.props.match.params.username);
  }

  componentDidUpdate(prevProps: SingleProfileDataRouteProps) {
    if (this.props.match.params.username !== prevProps.match.params.username) {
      this.props.onGetUserJid(this.props.match.params.username);
      this.props.onPopBreadcrumb(this.props.match.url.replace(/\/+$/, ''));
      this.props.onPushBreadcrumb(this.props.match.url, this.props.match.params.username);
    }
  }

  componentWillUnmount() {
    this.props.onClearUserJid();
    this.props.onPopBreadcrumb(this.props.match.url.replace(/\/+$/, ''));
  }

  render() {
    return null;
  }
}

export function createSingleProfileDataRoute(profileActions, breadcrumbsActions) {
  const mapDispatchToProps = {
    onGetUserJid: profileActions.getUserJid,
    onClearUserJid: profileActions.clearUserJid,
    onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
    onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
  };

  return withRouter<any>(connect(undefined, mapDispatchToProps)(SingleProfileDataRoute));
}

export default createSingleProfileDataRoute(injectedProfileActions, injectedBreadcrumbsActions);
