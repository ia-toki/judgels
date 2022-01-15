import { Redirect, Switch, withRouter } from 'react-router';

import { Topbar } from '../Topbar/Topbar';

function ContentAndTopbar({ contentHeader, topbarElement, contentElement }) {
  return (
    <div className="content-with-topbar">
      {contentHeader}
      {topbarElement}
      <hr />
      {contentElement}
    </div>
  );
}

function resolveUrl(parentPath, childPath) {
  const actualChildPath = childPath === '@' ? '' : childPath;
  return (parentPath + '/' + actualChildPath).replace(/\/\/+/g, '/');
}

function ContentWithTopbar({ match, location, contentHeader, items }) {
  const renderTopbar = () => {
    const topbarItems = items
      .filter(item => !item.disabled)
      .map(item => ({
        id: item.id,
        titleIcon: item.titleIcon,
        title: item.title,
      }));

    return <Topbar activeItemId={getActiveItemId()} items={topbarItems} onResolveItemUrl={onResolveItemUrl} />;
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
    <ContentAndTopbar contentHeader={contentHeader} topbarElement={renderTopbar()} contentElement={renderContent()} />
  );
}

export default withRouter(ContentWithTopbar);
