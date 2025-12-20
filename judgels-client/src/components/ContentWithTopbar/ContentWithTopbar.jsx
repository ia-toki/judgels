import classNames from 'classnames';
import { Redirect, Switch } from 'react-router';
import { useLocation, useRouteMatch } from 'react-router-dom';

import { Topbar } from '../Topbar/Topbar';

function ContentAndTopbar({ className, topbarElement, contentElement }) {
  return (
    <div className={classNames('content-with-topbar', className)}>
      {topbarElement}
      {contentElement}
    </div>
  );
}

function resolveUrl(parentPath, childPath) {
  const actualChildPath = childPath === '@' ? '' : childPath;
  return (parentPath + '/' + actualChildPath).replace(/\/\/+/g, '/');
}

export default function ContentWithTopbar({ className, items }) {
  const match = useRouteMatch();
  const location = useLocation();

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
      };
      if (item.component) {
        props.component = item.component;
      }
      if (item.render) {
        props.render = item.render;
      }
      return <RouteC key={item.id} {...props} />;
    });

    const redirect = items[0].id !== '@' && <Redirect exact from={match.url} to={resolveUrl(match.url, items[0].id)} />;

    return (
      <Switch>
        {redirect}
        {components}
      </Switch>
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

  return <ContentAndTopbar className={className} topbarElement={renderTopbar()} contentElement={renderContent()} />;
}
