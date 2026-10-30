import { ButtonGroup, Tab, Tabs } from '@blueprintjs/core';
import { Link } from '@tanstack/react-router';
import { useEffect, useRef } from 'react';

import { ButtonLink } from '../ButtonLink/ButtonLink';

import './Topbar.scss';

export function Topbar({ activeItemPath, items, secondary, onResolveItemUrl }) {
  const buttonGroupRef = useRef(null);

  // CSS cannot tell where the tabs wrap, so mark the ends of each row for Topbar.scss to round them.
  useEffect(() => {
    const el = buttonGroupRef.current;
    if (!el) return;
    const observer = new ResizeObserver(() => {
      const tabs = [...el.children];
      tabs.forEach((tab, i) => {
        tab.toggleAttribute('data-row-start', tabs[i - 1]?.offsetTop !== tab.offsetTop);
        tab.toggleAttribute('data-row-end', tabs[i + 1]?.offsetTop !== tab.offsetTop);
      });
    });
    observer.observe(el);
    return () => observer.disconnect();
  }, [items.length]);

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
    <ButtonGroup className="topbar" fill ref={buttonGroupRef}>
      {tabs}
    </ButtonGroup>
  );
}
