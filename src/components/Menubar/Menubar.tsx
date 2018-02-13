import { Tab2, Tabs2 } from '@blueprintjs/core';
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

export interface MenubarConnectedProps {
  onNavigate: (url: string) => void;
}

class MenubarContainer extends React.Component<RouteComponentProps<{}> & MenubarProps & MenubarConnectedProps> {
  render() {
    const selectedTabId = this.getActiveItemId();
    const homeRoute = this.props.homeRoute;

    return (
      <div className="menubar">
        <div className="menubar__content">
          <Tabs2 id="menubar" renderActiveTabPanelOnly selectedTabId={selectedTabId} onChange={this.onTabChange}>
            {homeRoute ? (
              <Tab2 key={homeRoute.id} id={homeRoute.id}>
                {homeRoute.title}
              </Tab2>
            ) : null}
            {this.props.items.map(item => {
              return (
                <Tab2 key={item.id} id={item.id}>
                  {item.title}
                </Tab2>
              );
            })}
          </Tabs2>
        </div>
      </div>
    );
  }

  private getActiveItemId = () => {
    const { items, location, homeRoute, match } = this.props;
    const relativePath = location.pathname.replace(match.path, '');
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

function createMenubarContainer() {
  const mapDispatchToProps = dispatch => ({
    onNavigate: (url: string) => dispatch(push(url)),
  });
  return connect(undefined, mapDispatchToProps)(MenubarContainer);
}

export default withRouter<any>(createMenubarContainer());
