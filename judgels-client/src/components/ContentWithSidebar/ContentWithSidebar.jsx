import classNames from 'classnames';
import { useLocation, useResolvedPath } from 'react-router-dom';

import { Sidebar } from '../Sidebar/Sidebar';

import './ContentWithSidebar.scss';

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
  return (parentPath + '/' + childPath).replace(/\/\/+/g, '/');
}

export default function ContentWithSidebar({
  items,
  title,
  action,
  contentHeader,
  stickyWidget1,
  stickyWidget2,
  smallContent,
  basePath,
  children,
}) {
  const location = useLocation();
  const { pathname: resolvedPathname } = useResolvedPath('');
  const pathname = basePath || resolvedPathname;

  const renderSidebar = () => {
    const sidebarItems = items
      .filter(item => !item.disabled)
      .map(item => ({
        path: item.path,
        titleIcon: item.titleIcon,
        title: item.title,
      }));
    const sidebarWidget = renderSidebarWidget();

    return (
      <Sidebar
        title={title}
        action={action}
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
    const activeItem = items.find(item => item.path === activeItemPath);

    if (!activeItem || !activeItem.widgetComponent) {
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

  const renderContent = () => {
    return (
      <div>
        {contentHeader}
        {children}
      </div>
    );
  };

  const onResolveItemUrl = itemPath => {
    return resolveUrl(pathname, itemPath);
  };

  const getActiveItemPath = () => {
    if (location.pathname === pathname) {
      return '';
    }

    const currentPath = location.pathname + '/';
    const nextSlashPos = currentPath.indexOf('/', pathname.length + 1);
    return currentPath.substring(pathname.length + 1, nextSlashPos);
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
