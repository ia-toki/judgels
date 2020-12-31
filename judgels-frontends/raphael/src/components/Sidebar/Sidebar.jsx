import { Icon, Popover, Position, Tab, Tabs } from '@blueprintjs/core';
import { PureComponent } from 'react';
import { Link } from 'react-router-dom';

import { Card } from '../Card/Card';

import './Sidebar.css';

export class Sidebar extends PureComponent {
  state = { isResponsivePopoverOpen: false };

  render() {
    const { title, action, activeItemId, items, widget, onResolveItemUrl } = this.props;

    const tabs = items.map(item => {
      const titleIcon = item.titleIcon && <Icon icon={item.titleIcon} />;
      const itemIcon = widget ? 'chevron-down' : 'chevron-right';

      const icon = item.id === activeItemId && (
        <Icon icon={itemIcon} iconSize={Icon.SIZE_LARGE} className="card-sidebar__arrow" />
      );

      return (
        <Tab key={item.id} id={item.id}>
          <Link to={onResolveItemUrl(item.id)}>
            {titleIcon}
            {titleIcon && <span>&nbsp;&nbsp;</span>}
            {item.title}
          </Link>
          {icon}
        </Tab>
      );
    });

    const tabsContainer = (
      <Tabs
        id="sidebar"
        selectedTabId={activeItemId}
        onChange={this.onResponsiveItemClick}
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
      <Popover
        className="card-sidebar card-sidebar__responsive"
        content={tabsContainer}
        position={Position.BOTTOM_LEFT}
        isOpen={this.state.isResponsivePopoverOpen}
        onInteraction={this.onResponsivePopoverInteraction}
        usePortal={false}
      >
        <div>
          <p className="card-sidebar__responsive-menu">
            <Icon icon="menu" iconSize={Icon.SIZE_LARGE} />
            &nbsp;<small>{title}</small>
          </p>
        </div>
      </Popover>
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
