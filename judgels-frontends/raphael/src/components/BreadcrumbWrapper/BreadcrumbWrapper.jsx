import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { PushBreadcrumb, PopBreadcrumb } from '../../modules/breadcrumbs/breadcrumbsReducer';

export function withBreadcrumb(breadcrumbTitle) {
  return InnerComponent => {
    class WrappedComponent extends React.Component {
      componentDidMount() {
        this.props.onPushBreadcrumb(this.props.match.url, breadcrumbTitle);
      }

      render() {
        const { location, match, onPushBreadcrumb, onPopBreadcrumb, ...props } = this.props;
        return <InnerComponent location={this.props.location} {...props} />;
      }

      componentWillUnmount() {
        this.props.onPopBreadcrumb(this.props.match.url);
      }
    }

    const mapDispatchToProps = {
      onPushBreadcrumb: (link, title) => PushBreadcrumb({ link, title }),
      onPopBreadcrumb: link => PopBreadcrumb({ link }),
    };

    return withRouter(connect(undefined, mapDispatchToProps)(WrappedComponent));
  };
}
