import { Icon, IconName, Tab, Tabs } from '@blueprintjs/core';
import * as React from 'react';

import { Card } from '../Card/Card';

import './Sidebar.css';

export interface SidebarItem {
  id: string;
  titleIcon?: IconName;
  title: string;
}

export interface SidebarProps {
  title: string;
  action?: JSX.Element;
  activeItemId: string;
  items: SidebarItem[];
  onItemClick: (parentPath: string, itemId: string) => void;
}

export class Sidebar extends React.Component<SidebarProps> {
  render() {
    const { title, action, activeItemId, items, onItemClick } = this.props;

    const tabs = items.map(item => {
      const titleIcon = item.titleIcon && <Icon icon={item.titleIcon} />;

      const icon = item.id === activeItemId && (
        <Icon icon="chevron-right" iconSize={Icon.SIZE_LARGE} className="card-sidebar__arrow" />
      );

      return (
        <Tab key={item.id} id={item.id}>
          <span>
            {titleIcon}
            {titleIcon && <span>&nbsp;&nbsp;</span>}
            {item.title}
          </span>
          {icon}
        </Tab>
      );
    });

    return (
      <Card className="card-sidebar" title={title} action={action} actionRightJustified>
        <Tabs id="sidebar" selectedTabId={activeItemId} onChange={onItemClick} vertical renderActiveTabPanelOnly>
          {tabs}
        </Tabs>
      </Card>
    );
  }
}
