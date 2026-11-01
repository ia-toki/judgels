import { Flex } from '@blueprintjs/labs';
import { useLocation } from '@tanstack/react-router';

import { resolveUrl } from '../../utils/url';
import { Topbar } from '../Topbar/Topbar';

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

  return (
    <Flex className={className} flexDirection="column" gap={gap}>
      {renderTopbar()}
      {children}
    </Flex>
  );
}
