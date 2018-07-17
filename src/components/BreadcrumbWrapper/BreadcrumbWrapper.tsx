import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { AddBreadcrumb, DelBreadcrumb } from '../../modules/breadcrumbs/breadcrumbsReducer';

export function withBreadcrumb(breadcrumbTitle: string) {
  return InnerComponent => {
    interface WrappedComponentConnectedProps {
      location: {
        pathname: string;
      };
      match: {
        url: string;
      };
      onPushBreadcrumb: (link: string, title: string) => Promise<void>;
      onPopBreadcrumb: (link: string) => Promise<void>;
    }

    class WrappedComponent extends React.Component<WrappedComponentConnectedProps> {
      async componentDidMount() {
        await this.props.onPushBreadcrumb(this.props.match.url, breadcrumbTitle);
      }

      render() {
        const { location, match, onPushBreadcrumb, onPopBreadcrumb, ...props } = this.props;
        return <InnerComponent location={this.props.location} {...props} />;
      }

      async componentWillUnmount() {
        await this.props.onPopBreadcrumb(this.props.match.url);
      }
    }

    const mapDispatchToProps = dispatch => ({
      onPushBreadcrumb: (link: string, title: string) => dispatch(AddBreadcrumb.create({ link, title })),
      onPopBreadcrumb: (link: string) => dispatch(DelBreadcrumb.create({ link })),
    });

    return withRouter<any>(connect(undefined, mapDispatchToProps)(WrappedComponent));
  };
}
