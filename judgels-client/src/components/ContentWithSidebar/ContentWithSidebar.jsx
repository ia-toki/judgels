import { Box, Flex } from '@blueprintjs/labs';
import { useLocation } from '@tanstack/react-router';
import classNames from 'classnames';

import { resolveUrl } from '../../utils/url';
import { ContentCard, PageContentContext } from '../ContentCard/ContentCard';
import { Sidebar } from '../Sidebar/Sidebar';

import './ContentWithSidebar.scss';

export default function ContentWithSidebar({
  items,
  title,
  contentHeader,
  contentBanner,
  stickyWidget: StickyWidget,
  smallContent,
  basePath,
  children,
}) {
  const location = useLocation();

  const renderSidebar = () => {
    const sidebarItems = items
      .filter(item => !item.disabled)
      .map(item => ({ ...item, children: item.children && item.children.filter(child => !child.disabled) }));
    const sidebarWidget = renderSidebarWidget();

    return (
      <Sidebar
        title={title}
        activeItemPath={getActiveItemPath()}
        items={sidebarItems}
        widget={sidebarWidget}
        onResolveItemUrl={onResolveItemUrl}
      />
    );
  };

  const renderStickyWidget = () => {
    if (!StickyWidget) {
      return null;
    }
    return (
      <div>
        <hr />
        <StickyWidget />
      </div>
    );
  };

  const renderSidebarWidget = () => {
    const activeItemPath = getActiveItemPath();
    const activeItem = items
      .flatMap(item => (item.children ? item.children : [item]))
      .find(item => item.path === activeItemPath);

    if (!activeItem || !activeItem.widgetComponent) {
      return null;
    }
    if (activeItem.widgetExact && getRelativePath() !== activeItem.path) {
      return null;
    }

    const Widget = activeItem.widgetComponent;
    return (
      <div>
        <hr />
        <Widget />
      </div>
    );
  };

  const onResolveItemUrl = itemPath => {
    return resolveUrl(basePath, itemPath);
  };

  const getRelativePath = () => {
    return location.pathname.slice(basePath.length).replace(/^\/|\/$/g, '');
  };

  const getActiveItemPath = () => {
    const relativePath = getRelativePath();

    const itemPaths = items.flatMap(item => (item.children ? item.children.map(child => child.path) : [item.path]));
    const matchedPath = itemPaths
      .filter(path => path && (relativePath === path || relativePath.startsWith(path + '/')))
      .sort((a, b) => b.length - a.length)[0];

    return matchedPath ?? relativePath.split('/')[0];
  };

  const responsive = window.matchMedia && window.matchMedia('(max-width: 750px)').matches;
  const stickyWidget = renderStickyWidget();

  return (
    <div className="content-with-sidebar">
      {contentHeader && <div className="content-with-sidebar__header">{contentHeader}</div>}
      <Box className="content-with-sidebar__sidebar" marginBottom={4}>
        {renderSidebar()}
        {!responsive && stickyWidget}
      </Box>
      <Flex
        className={classNames('content-with-sidebar__content', {
          'content-with-sidebar__content--small': smallContent,
        })}
        flexDirection="column"
        gap={2}
      >
        {contentBanner}
        <ContentCard className="content-with-sidebar__card">
          <div className="content-with-sidebar__card-content">
            <PageContentContext.Provider value={true}>{children}</PageContentContext.Provider>
          </div>
        </ContentCard>
        {responsive && stickyWidget}
      </Flex>
    </div>
  );
}
