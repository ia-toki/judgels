import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { PushBreadcrumb, PopBreadcrumb } from '../../modules/breadcrumbs/breadcrumbsReducer';

export function withBreadcrumb(breadcrumbTitle: string) {
  return InnerComponent => {
    interface WrappedComponentConnectedProps extends RouteComponentProps<{}> {
      onPushBreadcrumb: (link: string, title: string) => any;
      onPopBreadcrumb: (link: string) => any;
    }

    class WrappedComponent extends React.Component<WrappedComponentConnectedProps> {
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
      onPushBreadcrumb: (link: string, title: string) => PushBreadcrumb.create({ link, title }),
      onPopBreadcrumb: (link: string) => PopBreadcrumb.create({ link }),
    };

    return withRouter<any>(connect(undefined, mapDispatchToProps)(WrappedComponent));
  };
}
