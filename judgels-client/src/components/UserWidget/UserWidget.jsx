import { Alignment, Menu, MenuDivider, MenuItem, Navbar, Popover, Position } from '@blueprintjs/core';
import { ChevronDown, Menu as IconMenu } from '@blueprintjs/icons';
import { PureComponent } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';

import { isTLX } from '../../conf';
import { getRatingClass } from '../../modules/api/jophiel/userRating';
import { selectIsUserWebConfigLoaded, selectUserProfile } from '../../routes/jophiel/modules/userWebSelectors';
import MenuItemLink from '../MenuItemLink/MenuItemLink';

import * as avatarActions from '../../routes/jophiel/modules/avatarActions';

import './UserWidget.scss';

export class UserWidget extends PureComponent {
  state = { avatarUrl: undefined };

  componentDidMount() {
    this.refreshUser();
  }

  async componentDidUpdate(prevProps) {
    if (this.props.user !== prevProps.user) {
      this.refreshUser();
    }
  }

  render() {
    const { isWebConfigLoaded, profile } = this.props;
    if (!isWebConfigLoaded) {
      return null;
    }
    if (profile) {
      return this.renderForUser(this.state.avatarUrl, profile);
    } else {
      return this.renderForGuest();
    }
  }

  refreshUser = async () => {
    const { user, onRenderAvatar } = this.props;
    if (user) {
      const avatarUrl = await onRenderAvatar(user.jid);
      this.setState({ avatarUrl });
    }
  };

  renderForUser = (avatarUrl, profile) => {
    const menuItems = (
      <>
        <MenuItemLink text="My profile" to={`/profiles/${profile.username}`} />
        {isTLX() && <MenuItemLink text="My account" to="/account" />}
        <MenuItemLink text="Log out" to="/logout" />
      </>
    );

    const menu = <Menu className="widget-user__menu">{menuItems}</Menu>;

    const popover = (
      <Popover className="widget-user__avatar-menu" content={menu} position={Position.BOTTOM_RIGHT} usePortal={false}>
        <div className="widget-user__profile">
          <span className="widget-user__user__username">
            <span data-key="username" className={getRatingClass(profile.rating)}>
              {profile.username}
            </span>
          </span>{' '}
          <ChevronDown color="#252627" />
        </div>
      </Popover>
    );

    const responsiveMenu = (
      <Menu>
        <MenuItem text={this.props.homeRoute.title} to="/" />
        {this.props.items.map(item => (
          <MenuItemLink text={item.title} to={item.route.path} />
        ))}
        <MenuDivider className="widget-user__menu-helper" />
        {menuItems}
      </Menu>
    );

    const responsivePopover = (
      <Popover
        className="widget-user__burger"
        content={responsiveMenu}
        position={Position.BOTTOM_RIGHT}
        usePortal={false}
      >
        <IconMenu />
      </Popover>
    );

    return (
      <Navbar.Group align={Alignment.RIGHT}>
        <div className="widget-user__avatar-wrapper">
          <img src={avatarUrl} alt="avatar" className="widget-user__avatar" />
        </div>
        {popover}
        {responsivePopover}
      </Navbar.Group>
    );
  };

  renderForGuest = () => {
    return (
      <Navbar.Group align={Alignment.RIGHT}>
        <div className="widget-user__link">
          <Link data-key="login" to="/login">
            Log in
          </Link>
        </div>
        {isTLX() && (
          <div className="widget-user__link">
            <Link data-key="register" to="/register">
              Register
            </Link>
          </div>
        )}
        {this.renderGuestResponsiveMenu()}
      </Navbar.Group>
    );
  };

  renderGuestResponsiveMenu = () => {
    const menu = (
      <Menu className="widget-user__menu">
        <MenuItem text={this.props.homeRoute.title} to="/" />
        {this.props.items.map(item => (
          <MenuItemLink text={item.title} to={item.route.path} />
        ))}
      </Menu>
    );

    return (
      <Popover className="widget-user__burger" content={menu} position={Position.BOTTOM_RIGHT} usePortal={false}>
        <IconMenu />
      </Popover>
    );
  };
}

const mapStateToProps = state => ({
  user: state.session.user,
  isWebConfigLoaded: selectIsUserWebConfigLoaded(state),
  profile: selectUserProfile(state),
});

const mapDispatchToProps = {
  onRenderAvatar: avatarActions.renderAvatar,
};

export default connect(mapStateToProps, mapDispatchToProps)(UserWidget);
