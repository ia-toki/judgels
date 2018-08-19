import { Tab, Tabs } from '@blueprintjs/core';
import * as React from 'react';
import { RouteComponentProps, RouteProps, withRouter } from 'react-router';
import { matchPath, Link } from 'react-router-dom';

import './Menubar.css';

export interface MenubarItem {
  id: string;
  title: string;
  route: RouteProps;
}

export interface MenubarProps {
  items: MenubarItem[];
  homeRoute?: MenubarItem;
}

class Menubar extends React.Component<MenubarProps & RouteComponentProps<{}>> {
  render() {
    const selectedTabId = this.getActiveItemId();
    const homeRoute = this.props.homeRoute;

    return (
      <div className="menubar">
        <div className="menubar__content">
          <Tabs id="menubar" renderActiveTabPanelOnly animate={false} selectedTabId={selectedTabId}>
            {homeRoute ? (
              <Tab key={homeRoute.id} id={homeRoute.id}>
                <Link to={this.resolveUrl(homeRoute.id)}>{homeRoute.title}</Link>
              </Tab>
            ) : null}
            {this.props.items.map(item => {
              return (
                <Tab key={item.id} id={item.id}>
                  <Link to={this.resolveUrl(item.id)}>{item.title}</Link>
                </Tab>
              );
            })}
          </Tabs>
        </div>
      </div>
    );
  }

  private getActiveItemId = () => {
    const { items, location, homeRoute, match } = this.props;
    const relativePath = match.path === '/' ? location.pathname : location.pathname.slice(match.path.length);
    const matchingItem = items.find(item => matchPath(relativePath, item.route) !== null);
    if (matchingItem) {
      return matchingItem.id;
    } else if (homeRoute) {
      return homeRoute.id;
    } else {
      return items[0].id;
    }
  };

  private resolveUrl = newTabId => {
    const { homeRoute, items, match } = this.props;
    let newTabItem;
    if (homeRoute && homeRoute.id === newTabId) {
      newTabItem = homeRoute;
    } else {
      newTabItem = items.find(item => item.id === newTabId);
    }
    if (!newTabItem) {
      return '';
    }
    return (match.path === '/' ? '' : match.path) + (newTabItem.route.path ? newTabItem.route.path : '/');
  };
}

export default withRouter<any>(Menubar);
