import { Button, Popover, Position } from '@blueprintjs/core';
import { Menu } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { Link } from '@tanstack/react-router';
import classNames from 'classnames';
import { useState } from 'react';

import './Sidebar.scss';

export function Sidebar({ className, title, activeItemPath, items, widget, compact, onResolveItemUrl }) {
  const [isResponsiveMenuOpen, setIsResponsiveMenuOpen] = useState(false);

  const renderItem = (item, { compact, onClick }) => {
    const isActive = item.path === activeItemPath;

    return (
      <Flex key={item.path} asChild alignItems="center" gap={2}>
        <Link
          className={classNames('sidebar__item', {
            'sidebar__item--active': isActive,
            'sidebar__item--muted': item.muted,
          })}
          to={onResolveItemUrl(item.path)}
          activeOptions={{ exact: true }}
          aria-current={isActive ? 'page' : undefined}
          onClick={onClick}
        >
          {item.titleIcon}
          <span className="sidebar__item-title">{compact ? item.compactTitle : item.title}</span>
          {item.trailing}
        </Link>
      </Flex>
    );
  };

  const renderItems = options => (
    <Flex asChild flexDirection="column" gap={0.5}>
      <nav aria-label={title}>
        {items.flatMap(item => {
          if (item.children) {
            return [
              <div key={item.title} className="sidebar__group">
                {item.title}
              </div>,
              ...item.children.map(child => renderItem(child, options)),
            ];
          }
          return [renderItem(item, options)];
        })}
      </nav>
    </Flex>
  );

  return (
    <div className={classNames('sidebar', className)}>
      <div className="sidebar__full">{renderItems({ compact })}</div>
      <div className="sidebar__responsive">
        <Popover
          content={
            <div className="sidebar__responsive-items">
              {renderItems({ onClick: () => setIsResponsiveMenuOpen(false) })}
            </div>
          }
          position={Position.BOTTOM_LEFT}
          isOpen={isResponsiveMenuOpen}
          onInteraction={setIsResponsiveMenuOpen}
          usePortal={false}
        >
          <Button small minimal icon={<Menu />}>
            {title}
          </Button>
        </Popover>
      </div>
      {widget}
    </div>
  );
}
