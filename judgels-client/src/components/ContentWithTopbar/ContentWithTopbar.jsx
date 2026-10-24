import { Flex } from '@blueprintjs/labs';
import { useLocation } from '@tanstack/react-router';
import classNames from 'classnames';

import { Topbar } from '../Topbar/Topbar';

function ContentAndTopbar({ className, gap, topbarElement, contentElement }) {
  return (
    <Flex className={classNames('content-with-topbar', className)} flexDirection="column" gap={gap}>
      {topbarElement}
      {contentElement}
    </Flex>
  );
}

function resolveUrl(parentPath, childPath) {
  return (parentPath + '/' + childPath).replace(/\/\/+/g, '/');
}

export default function ContentWithTopbar({ className, items, basePath, secondary, gap = 5, children }) {
  const location = useLocation();

  const renderTopbar = () => {
    const topbarItems = items
      .filter(item => !item.disabled)
      .map(item => ({
        path: item.path,
        titleIcon: item.titleIcon,
        title: item.title,
      }));

    return (
      <Topbar
        activeItemPath={getActiveItemPath()}
        items={topbarItems}
        secondary={secondary}
        onResolveItemUrl={onResolveItemUrl}
      />
    );
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

  return <ContentAndTopbar className={className} gap={gap} topbarElement={renderTopbar()} contentElement={children} />;
}
