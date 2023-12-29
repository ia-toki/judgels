import { Tab, Tabs } from '@blueprintjs/core';
import { withRouter } from 'react-router';
import { Link, matchPath } from 'react-router-dom';

import './Menubar.scss';

function Menubar({ match, location, items, homeRoute }) {
  const getActiveItemId = () => {
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
    return (match.path === '/' ? '' : match.path) + (newTabItem.route.path ? newTabItem.route.path : '/');
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

export default withRouter(Menubar);
