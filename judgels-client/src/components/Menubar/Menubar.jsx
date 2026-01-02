import { Tab, Tabs } from '@blueprintjs/core';
import { Link, useLocation } from '@tanstack/react-router';

import './Menubar.scss';

export default function Menubar({ items, homeRoute }) {
  const location = useLocation();

  const getActiveItemId = () => {
    const matchingItem = items.find(item => {
      const itemPath = item.route.path;
      return location.pathname === itemPath || location.pathname.startsWith(itemPath + '/');
    });
    return matchingItem?.id || homeRoute?.id || items[0].id;
  };

  const resolveUrl = newTabId => {
    let newTabItem;
    if (homeRoute && homeRoute.id === newTabId) {
      newTabItem = homeRoute;
    } else {
      newTabItem = items.find(item => item.id === newTabId);
    }
    if (!newTabItem) {
      return '';
    }
    // Use absolute path since routes are defined with absolute paths
    return newTabItem.route.path || '/';
  };

  const selectedTabId = getActiveItemId();

  return (
    <div className="menubar">
      <Tabs id="menubar" renderActiveTabPanelOnly animate={false} selectedTabId={selectedTabId}>
        {homeRoute ? (
          <Tab key={homeRoute.id} id={homeRoute.id}>
            <Link to={resolveUrl(homeRoute.id)}>{homeRoute.title}</Link>
          </Tab>
        ) : null}
        {items.map(item => {
          return (
            <Tab key={item.id} id={item.id}>
              <Link to={resolveUrl(item.id)}>{item.title}</Link>
            </Tab>
          );
        })}
      </Tabs>
    </div>
  );
}
