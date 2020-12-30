import classNames from 'classnames';
import { Redirect, Switch, withRouter } from 'react-router';

import { Sidebar } from '../Sidebar/Sidebar';

import './ContentWithSidebar.css';

function ContentAndSidebar({ sidebarElement, contentElement, stickyWidget, smallContent }) {
  const responsive = window.matchMedia && window.matchMedia('(max-width: 750px)').matches;
  return (
    <div className="content-with-sidebar">
      <div className="content-with-sidebar__sidebar">
        {sidebarElement}
        {!responsive && stickyWidget}
      </div>
      <div
        className={classNames('content-with-sidebar__content', {
          'content-with-sidebar__content--small': smallContent,
        })}
      >
        {contentElement}
        {responsive && stickyWidget}
      </div>
    </div>
  );
}

function resolveUrl(parentPath, childPath) {
  const actualChildPath = childPath === '@' ? '' : childPath;
  return (parentPath + '/' + actualChildPath).replace(/\/\/+/g, '/');
}

function ContentWithSidebar({ match, location, items, title, action, contentHeader, stickyWidget, smallContent }) {
  const renderSidebar = () => {
    const sidebarItems = items
      .filter(item => !item.disabled)
      .map(item => ({
        id: item.id,
        titleIcon: item.titleIcon,
        title: item.title,
      }));
    const sidebarWidget = renderSidebarWidget();

    return (
      <Sidebar
        title={title}
        action={action}
        activeItemId={getActiveItemId()}
        items={sidebarItems}
        widget={sidebarWidget}
        onResolveItemUrl={onResolveItemUrl}
      />
    );
  };

  const renderStickyWidget = () => {
    if (stickyWidget) {
      const Widget = stickyWidget;
      return (
        <div>
          <hr />
          <Widget />
        </div>
      );
    }
    return null;
  };

  const renderSidebarWidget = () => {
    const components = items
      .filter(item => !!item.widgetComponent)
      .map(item => {
        const RouteC = item.routeComponent;
        const props = {
          exact: item.id === '@',
          path: resolveUrl(match.url, item.id),
          component: item.widgetComponent,
        };
        return <RouteC key={item.id} {...props} />;
      });

    if (components.length === 0) {
      return null;
    }

    return (
      <div>
        <hr />
        <Switch>{components}</Switch>
      </div>
    );
  };

  const renderContent = () => {
    const components = items.map(item => {
      const RouteC = item.routeComponent;
      const props = {
        exact: item.id === '@',
        path: resolveUrl(match.url, item.id),
        component: item.component,
      };
      return <RouteC key={item.id} {...props} />;
    });

    const redirect = items[0].id !== '@' && <Redirect exact from={match.url} to={resolveUrl(match.url, items[0].id)} />;

    return (
      <div>
        {contentHeader}
        <Switch>
          {redirect}
          {components}
        </Switch>
      </div>
    );
  };

  const onResolveItemUrl = itemId => {
    return resolveUrl(match.url, itemId);
  };

  const getActiveItemId = () => {
    if (location.pathname === match.url) {
      return '@';
    }

    const currentPath = location.pathname + '/';
    const nextSlashPos = currentPath.indexOf('/', match.url.length + 1);
    return currentPath.substring(match.url.length + 1, nextSlashPos);
  };

  return (
    <ContentAndSidebar
      sidebarElement={renderSidebar()}
      contentElement={renderContent()}
      stickyWidget={renderStickyWidget()}
      smallContent={smallContent}
    />
  );
}

export default withRouter(ContentWithSidebar);
