import { Tab, Tabs } from '@blueprintjs/core';
import { Link, useLocation } from 'react-router-dom';

import './Menubar.scss';

export default function Menubar({ items, homeRoute }) {
  const location = useLocation();

  const getActiveItemId = () => {
    // Check each item to find the one matching the current path
    // In React Router 6, we need to check if location.pathname starts with the item path
    // The basePath gives us the current matched route, but we need to check child paths

    const matchingItem = items.find(item => {
      const itemPath = item.route.path || '/';
      // Check if pathname contains this item's path
      // First try to match against the end of the pathname (relative match)
      const pathnameEnding = '/' + location.pathname.split('/').filter(Boolean).pop();
      if (pathnameEnding === itemPath || pathnameEnding.startsWith(itemPath + '/')) {
        return true;
      }
      // Also try direct match in case of absolute paths
      return location.pathname === itemPath || location.pathname.startsWith(itemPath + '/');
    });
    if (matchingItem) {
      return matchingItem.id;
    } else if (homeRoute) {
      return homeRoute.id;
    } else {
      return items[0].id;
    }
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
    // Use relative path - React Router will resolve it relative to current route
    return newTabItem.route.path ? '.' + newTabItem.route.path : '.';
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
