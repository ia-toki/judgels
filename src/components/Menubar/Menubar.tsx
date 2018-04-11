import { Tab, Tabs } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, RouteProps } from 'react-router';
import { withRouter, matchPath } from 'react-router-dom';
import { push } from 'react-router-redux';

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

export interface MenubarConnectedProps extends RouteComponentProps<{}> {
  onNavigate: (url: string) => any;
}

class Menubar extends React.Component<MenubarProps & MenubarConnectedProps> {
  render() {
    const selectedTabId = this.getActiveItemId();
    const homeRoute = this.props.homeRoute;

    return (
      <div className="menubar">
        <div className="menubar__content">
          <Tabs id="menubar" renderActiveTabPanelOnly selectedTabId={selectedTabId} onChange={this.onTabChange}>
            {homeRoute ? (
              <Tab key={homeRoute.id} id={homeRoute.id}>
                {homeRoute.title}
              </Tab>
            ) : null}
            {this.props.items.map(item => {
              return (
                <Tab key={item.id} id={item.id}>
                  {item.title}
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

  private onTabChange = newTabId => {
    const { homeRoute, items, match } = this.props;
    let newTabItem;
    if (homeRoute && homeRoute.id === newTabId) {
      newTabItem = homeRoute;
    } else {
      newTabItem = items.find(item => item.id === newTabId);
    }
    if (!newTabItem) {
      return;
    }
    const path = (match.path === '/' ? '' : match.path) + (newTabItem.route.path ? newTabItem.route.path : '/');
    this.props.onNavigate(path);
  };
}

function createMenubar() {
  const mapDispatchToProps = {
    onNavigate: push,
  };
  return withRouter<any>(connect(undefined, mapDispatchToProps)(Menubar));
}

export default createMenubar();
