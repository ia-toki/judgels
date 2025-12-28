import { ButtonGroup } from '@blueprintjs/core';

import { ButtonLink } from '../ButtonLink/ButtonLink';

import './Topbar.scss';

export function Topbar({ activeItemPath, items, onResolveItemUrl }) {
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
