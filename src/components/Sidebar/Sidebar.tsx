import { Icon, Tab2, Tabs2 } from '@blueprintjs/core';
import * as React from 'react';

import { Card } from '../Card/Card';

import './Sidebar.css';

export interface SidebarItem {
  id: string;
  title: string;
}

export interface SidebarProps {
  title: string;
  activeItemId: string;
  items: SidebarItem[];
  onItemClick: (parentPath: string, itemId: string) => void;
}

export class Sidebar extends React.Component<SidebarProps> {
  render() {
    const { title, activeItemId, items, onItemClick } = this.props;

    const tabs = items.map(item => {
      const icon = item.id === activeItemId && (
        <Icon iconName="chevron-right" iconSize={Icon.SIZE_LARGE} className="card-sidebar__arrow" />
      );

      return (
        <Tab2 key={item.id} id={item.id}>
          <span>{item.title}</span>
          {icon}
        </Tab2>
      );
    });

    return (
      <Card className="card-sidebar" title={title}>
        <Tabs2 id="sidebar" selectedTabId={activeItemId} onChange={onItemClick} vertical renderActiveTabPanelOnly>
          {tabs}
        </Tabs2>
      </Card>
    );
  }
}
