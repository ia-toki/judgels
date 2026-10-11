import { ButtonGroup, Tab, Tabs } from '@blueprintjs/core';
import { Link } from '@tanstack/react-router';

import { ButtonLink } from '../ButtonLink/ButtonLink';

import './Topbar.scss';

export function Topbar({ activeItemPath, items, secondary, onResolveItemUrl }) {
  if (secondary) {
    return (
      <Tabs id="topbar" className="topbar-secondary" selectedTabId={activeItemPath} renderActiveTabPanelOnly>
        {items.map(item => (
          <Tab key={item.path} id={item.path}>
            <Link to={onResolveItemUrl(item.path)}>
              {item.titleIcon}
              <span className="topbar__item">{item.title}</span>
            </Link>
          </Tab>
        ))}
      </Tabs>
    );
  }

  const tabs = items.map(item => {
    return (
      <ButtonLink key={item.path} id={item.path} active={item.path === activeItemPath} to={onResolveItemUrl(item.path)}>
        {item.titleIcon}
        <span className="topbar__item">{item.title}</span>
      </ButtonLink>
    );
  });

  return (
    <ButtonGroup className="topbar" fill>
      {tabs}
    </ButtonGroup>
  );
}
