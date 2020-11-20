import * as React from 'react';
import { withRouter } from 'react-router';
import { connect } from 'react-redux';

import * as breadcrumbsActions from '../../../../modules/breadcrumbs/breadcrumbsActions';
import * as profileActions from '../../modules/profileActions';

class SingleProfileDataRoute extends React.Component {
  componentDidMount() {
    this.props.onGetUser(this.props.match.params.username);
    this.props.onPushBreadcrumb(this.props.match.url, this.props.match.params.username);
  }

  componentDidUpdate(prevProps) {
    if (this.props.match.params.username !== prevProps.match.params.username) {
      this.props.onGetUser(this.props.match.params.username);
      this.props.onPopBreadcrumb(
        this.props.match.url.replace(
          new RegExp(`/${this.props.match.params.username}/*$`),
          `/${prevProps.match.params.username}`
        )
      );
      this.props.onPushBreadcrumb(this.props.match.url, this.props.match.params.username);
    }
  }

  componentWillUnmount() {
    this.props.onClearUser();
    this.props.onPopBreadcrumb(this.props.match.url);
  }

  render() {
    return null;
  }
}

const mapDispatchToProps = {
  onGetUser: profileActions.getUser,
  onClearUser: profileActions.clearUser,
  onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
  onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
};

export default withRouter(connect(undefined, mapDispatchToProps)(SingleProfileDataRoute));
