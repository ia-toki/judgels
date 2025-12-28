import classNames from 'classnames';
import { useLocation } from 'react-router';

import { useBreadcrumbsPath } from '../../hooks/useBreadcrumbsPath';
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
  const computedBasePath = useBreadcrumbsPath();
  const pathname = basePath || computedBasePath;

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
    return resolveUrl(pathname, itemPath);
  };

  const getActiveItemPath = () => {
    if (location.pathname === pathname || location.pathname === pathname + '/') {
      return '';
    }

    const currentPath = location.pathname + '/';
    const nextSlashPos = currentPath.indexOf('/', pathname.length + 1);
    return currentPath.substring(pathname.length + 1, nextSlashPos);
  };

  return <ContentAndTopbar className={className} topbarElement={renderTopbar()} contentElement={children} />;
}
