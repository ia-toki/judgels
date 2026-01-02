import { useLocation } from '@tanstack/react-router';
import classNames from 'classnames';

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
  return (parentPath + '/' + childPath).replace(/\/\/+/g, '/');
}

export default function ContentWithTopbar({ className, items, basePath, children }) {
  const location = useLocation();

  const renderTopbar = () => {
    const topbarItems = items
      .filter(item => !item.disabled)
      .map(item => ({
        path: item.path,
        titleIcon: item.titleIcon,
        title: item.title,
      }));

    return <Topbar activeItemPath={getActiveItemPath()} items={topbarItems} onResolveItemUrl={onResolveItemUrl} />;
  };

  const onResolveItemUrl = itemPath => {
    return resolveUrl(basePath, itemPath);
  };

  const getActiveItemPath = () => {
    if (location.pathname === basePath || location.pathname === basePath + '/') {
      return '';
    }

    const currentPath = location.pathname + '/';
    const nextSlashPos = currentPath.indexOf('/', basePath.length + 1);
    return currentPath.substring(basePath.length + 1, nextSlashPos);
  };

  return <ContentAndTopbar className={className} topbarElement={renderTopbar()} contentElement={children} />;
}
