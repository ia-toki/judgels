import { ButtonGroup } from '@blueprintjs/core';

import { ButtonLink } from '../ButtonLink/ButtonLink';

import './Topbar.scss';

export function Topbar({ activeItemId, items, onResolveItemUrl }) {
  const tabs = items.map(item => {
    return (
      <ButtonLink key={item.id} id={item.id} active={item.id === activeItemId} to={onResolveItemUrl(item.id)}>
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
