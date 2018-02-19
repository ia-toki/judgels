import * as classNames from 'classnames';
import * as React from 'react';
import { connect } from 'react-redux';
import { Redirect, RouteComponentProps, Switch, withRouter } from 'react-router';
import { push } from 'react-router-redux';

import { Sidebar } from '../Sidebar/Sidebar';

import './ContentWithSidebar.css';

export interface ContentAndSidebarProps {
  sidebarElement: JSX.Element;
  contentElement: JSX.Element;
  smallContent?: boolean;
}

const ContentAndSidebar = (props: ContentAndSidebarProps) => (
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

export interface ContentWithSidebarItem {
  id: string;
  title: string;
  routeComponent: any;
  component: any;
}

export interface ContentWithSidebarProps {
  title: string;
  smallContent?: boolean;
  items: ContentWithSidebarItem[];
}

interface ContentWithSidebarConnectedProps extends RouteComponentProps<{ pathname: string }> {
  onItemClick: (parentPath: string, itemId: string) => any;
}

function resolveUrl(parentPath: string, childPath: string) {
  return (parentPath + '/' + childPath).replace(/\/\/+/g, '/');
}

class ContentWithSidebar extends React.Component<ContentWithSidebarProps & ContentWithSidebarConnectedProps> {
  render() {
    return (
      <ContentAndSidebar
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
        path: resolveUrl(this.props.match.url, item.id),
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

function createContentWithSidebar() {
  const mapDispatchToProps = {
    onItemClick: (parentPath: string, itemId: string) => push(resolveUrl(parentPath, itemId)),
  };
  return connect(undefined, mapDispatchToProps)(ContentWithSidebar);
}

export default withRouter<any>(createContentWithSidebar());
