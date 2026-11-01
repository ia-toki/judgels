import { Box, Flex } from '@blueprintjs/labs';
import { useLocation } from '@tanstack/react-router';
import classNames from 'classnames';

import { ContentCard, PageContentContext } from '../ContentCard/ContentCard';
import { Sidebar } from '../Sidebar/Sidebar';

import './ContentWithSidebar.scss';

function ContentAndSidebar({
  headerElement,
  sidebarElement,
  bannerElement,
  contentElement,
  stickyWidget,
  smallContent,
}) {
  const responsive = window.matchMedia && window.matchMedia('(max-width: 750px)').matches;
  return (
    <div className="content-with-sidebar">
      {headerElement && <div className="content-with-sidebar__header">{headerElement}</div>}
      <Box className="content-with-sidebar__sidebar" marginBottom={4}>
        {sidebarElement}
        {!responsive && stickyWidget}
      </Box>
      <Flex
        className={classNames('content-with-sidebar__content', {
          'content-with-sidebar__content--small': smallContent,
        })}
        flexDirection="column"
        gap={2}
      >
        {bannerElement}
        <ContentCard className="content-with-sidebar__card">
          <div className="content-with-sidebar__card-content">
            <PageContentContext.Provider value={true}>{contentElement}</PageContentContext.Provider>
          </div>
        </ContentCard>
        {responsive && stickyWidget}
      </Flex>
    </div>
  );
}

function resolveUrl(parentPath, childPath) {
  return (parentPath + '/' + childPath).replace(/\/\/+/g, '/');
}

export default function ContentWithSidebar({
  items,
  title,
  contentHeader,
  contentBanner,
  stickyWidget1,
  stickyWidget2,
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
    let widget1 = null;
    if (stickyWidget1) {
      const Widget = stickyWidget1;
      widget1 = <Widget />;
    }

    let widget2 = null;
    if (stickyWidget2) {
      const Widget = stickyWidget2;
      widget2 = <Widget />;
    }

    if (stickyWidget1 || stickyWidget2) {
      return (
        <div>
          <hr />
          {widget1}
          {widget2}
        </div>
      );
    }
    return null;
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

  return (
    <ContentAndSidebar
      headerElement={contentHeader}
      sidebarElement={renderSidebar()}
      bannerElement={contentBanner}
      contentElement={children}
      stickyWidget={renderStickyWidget()}
      smallContent={smallContent}
    />
  );
}
