import { Icon, Menu, MenuDivider, MenuItem, Popover, Position } from '@blueprintjs/core';
import * as React from 'react';
import { Link } from 'react-router-dom';
import { connect } from 'react-redux';

import { User } from '../../modules/api/jophiel/user';
import { AppState } from '../../modules/store';
import MenuItemLink from '../MenuItemLink/MenuItemLink';

import './UserWidget.css';

export interface UserWidgetProps {
  user?: User;
}

export class UserWidget extends React.Component<UserWidgetProps> {
  render() {
    if (this.props.user) {
      return this.renderForUser(this.props.user);
    } else {
      return this.renderForGuest();
    }
  }

  private renderForUser = (user: User) => {
    const menu = (
      <Menu className="widget-user__menu">
        <MenuItem className="widget-user__menu-helper" iconName="user" text={user.username} disabled />
        <MenuDivider className="widget-user__menu-helper" />
        <MenuItemLink text="My account" to="/account" />
        <MenuItemLink text="Log out" to="/logout" />
      </Menu>
    );

    const popover = (
      <Popover className="widget-user__avatar-menu" content={menu} position={Position.BOTTOM_RIGHT} inline>
        <div>
          <span data-key="username" className="widget-user__user__username">
            {user.username}
          </span>{' '}
          <Icon iconName="pt-icon-chevron-down" />
        </div>
      </Popover>
    );

    const responsivePopover = (
      <Popover
        className="widget-user__burger"
        content={menu}
        position={Position.BOTTOM_RIGHT}
        inline
        useSmartArrowPositioning={false}
      >
        <Icon iconName="menu" iconSize={Icon.SIZE_LARGE} />
      </Popover>
    );

    return (
      <div className="pt-navbar-group pt-align-right">
        <img src={user.avatarUrl} className="widget-user__avatar" />
        {popover}
        {responsivePopover}
      </div>
    );
  };

  private renderForGuest = () => {
    return (
      <div className="pt-navbar-group pt-align-right">
        <div className="widget-user__link">
          <Link data-key="login" to="/login">
            Log in
          </Link>
        </div>
        <div className="widget-user__link">
          <Link data-key="register" to="/register">
            Register
          </Link>
        </div>
        {this.renderGuestResponsiveMenu()}
      </div>
    );
  };

  private renderGuestResponsiveMenu = () => {
    const menu = (
      <Menu className="widget-user__menu">
        <MenuItemLink text="Log in" to="/login" />
        <MenuItemLink text="Register" to="/register" />
      </Menu>
    );

    return (
      <Popover
        className="widget-user__burger"
        content={menu}
        position={Position.BOTTOM_RIGHT}
        inline
        useSmartArrowPositioning={false}
      >
        <Icon iconName="menu" iconSize={Icon.SIZE_LARGE} />
      </Popover>
    );
  };
}

export function createUserWidgetContainer() {
  const mapStateToProps = (state: AppState) => ({
    user: state.session.user,
  });

  // https://github.com/DefinitelyTyped/DefinitelyTyped/issues/19989
  const UserWidgetWrapper = (props: UserWidgetProps) => <UserWidget {...props} />;

  return connect(mapStateToProps)(UserWidgetWrapper);
}

const UserWidgetContainer = createUserWidgetContainer();
export default UserWidgetContainer;
