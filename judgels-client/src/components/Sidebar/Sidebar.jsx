import { Popover, Position, Tab, Tabs } from '@blueprintjs/core';
import { ChevronDown, ChevronRight, Menu } from '@blueprintjs/icons';
import { PureComponent } from 'react';
import { Link } from 'react-router';

import { Card } from '../Card/Card';

import './Sidebar.scss';

export class Sidebar extends PureComponent {
  state = { isResponsivePopoverOpen: false };

  render() {
    const { title, action, activeItemPath, items, widget, onResolveItemUrl } = this.props;

    const tabs = items.map(item => {
      const ItemIcon = widget ? ChevronDown : ChevronRight;

      const icon = item.path === activeItemPath && <ItemIcon className="card-sidebar__arrow" />;

      return (
        <Tab key={item.path} id={item.path}>
          <Link to={onResolveItemUrl(item.path)}>
            {item.titleIcon}
            {item.titleIcon && <span>&nbsp;&nbsp;</span>}
            {item.title}
          </Link>
          {icon}
        </Tab>
      );
    });

    const tabsContainer = (
      <Tabs
        id="sidebar"
        selectedTabId={activeItemPath}
        onChange={this.onResponsiveItemClick}
        animate={!this.state.isResponsivePopoverOpen}
        vertical
        renderActiveTabPanelOnly
      >
        {tabs}
      </Tabs>
    );

    const card = (
      <Card className="card-sidebar card-sidebar__full" title={title} action={action} actionRightJustified>
        {tabsContainer}
      </Card>
    );

    const responsiveMenu = (
      <div className="card-sidebar card-sidebar__responsive">
        <Popover
          content={tabsContainer}
          position={Position.BOTTOM_LEFT}
          isOpen={this.state.isResponsivePopoverOpen}
          onInteraction={this.onResponsivePopoverInteraction}
          usePortal={false}
        >
          <div>
            <p className="card-sidebar__responsive-menu">
              <Menu />
              &nbsp;<small>{title}</small>
            </p>
          </div>
        </Popover>
      </div>
    );

    return (
      <div>
        {card}
        {responsiveMenu}
        {widget}
      </div>
    );
  }

  onResponsivePopoverInteraction = state => {
    this.setState({ isResponsivePopoverOpen: state });
  };

  onResponsiveItemClick = () => {
    setTimeout(() => {
      this.setState({ isResponsivePopoverOpen: false });
    }, 200);
  };
}
