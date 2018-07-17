import * as classNames from 'classnames';
import * as React from 'react';
import { Redirect, Switch, withRouter } from 'react-router';
import { push } from 'react-router-redux';
import { connect } from 'react-redux';

import { Sidebar } from '../Sidebar/Sidebar';

import './ContentWithSidebar.css';

export interface ContentWithSidebarProps {
  sidebarElement: JSX.Element;
  contentElement: JSX.Element;
  smallContent?: boolean;
}

export const ContentWithSidebar = (props: ContentWithSidebarProps) => (
  <div className="content-with-sidebar">
    <div className="content-with-sidebar__sidebar">{props.sidebarElement}</div>
    <div
      className={classNames('content-with-sidebar__content', {
        'content-with-sidebar__content--small': props.smallContent,
      })}
    >
      {props.contentElement}
    </div>
  </div>
);

export interface ContentWithSidebarContainerItem {
  id: string;
  title: string;
  routeComponent: any;
  component: any;
}

export interface ContentWithSidebarContainerProps {
  title: string;
  smallContent?: boolean;
  items: ContentWithSidebarContainerItem[];
}

interface ContentWithSidebarContainerConnectedProps {
  location: {
    pathname: string;
  };
  match: {
    url: string;
  };

  onItemClick: (parentPath: string, itemId: string) => void;
}

function resolveUrl(parentPath: string, childPath: string) {
  return (parentPath + '/' + childPath).replace(/\/\/+/g, '/');
}

class ContentWithSidebarContainer extends React.Component<
  ContentWithSidebarContainerProps & ContentWithSidebarContainerConnectedProps
> {
  render() {
    return (
      <ContentWithSidebar
        sidebarElement={this.renderSidebar()}
        contentElement={this.renderContent()}
        smallContent={this.props.smallContent}
      />
    );
  }

  private renderSidebar = () => {
    const sidebarItems = this.props.items.map(item => ({
      id: item.id,
      title: item.title,
    }));

    return (
      <Sidebar
        title={this.props.title}
        activeItemId={this.getActiveItemId()}
        items={sidebarItems}
        onItemClick={this.onItemClick}
      />
    );
  };

  private renderContent = () => {
    const components = this.props.items.map(item => {
      const RouteC = item.routeComponent;
      const props = {
        exact: true,
        path: this.props.match.url + '/' + item.id,
        component: item.component,
      };
      return <RouteC key={item.id} {...props} />;
    });

    return (
      <div>
        <Switch>
          <Redirect exact from={this.props.match.url} to={resolveUrl(this.props.match.url, this.props.items[0].id)} />
          {components}
        </Switch>
      </div>
    );
  };

  private onItemClick = (itemId: string) => {
    return this.props.onItemClick(this.props.match.url, itemId);
  };

  private getActiveItemId = () => {
    const currentPath = this.props.location.pathname + '/';
    const nextSlashPos = currentPath.indexOf('/', this.props.match.url.length + 1);
    return currentPath.substring(this.props.match.url.length + 1, nextSlashPos);
  };
}

function createContentWithSidebarContainer() {
  const mapDispatchToProps = dispatch => ({
    onItemClick: (parentPath: string, itemId: string) => dispatch(push(resolveUrl(parentPath, itemId))),
  });
  return connect(undefined, mapDispatchToProps)(ContentWithSidebarContainer);
}

export default withRouter<any>(createContentWithSidebarContainer());
