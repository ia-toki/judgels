import { Alignment, Icon, Menu, MenuDivider, MenuItem, Navbar, Popover, Position } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';

import { APP_CONFIG, Mode } from '../../conf';
import MenuItemLink from '../MenuItemLink/MenuItemLink';
import { User } from '../../modules/api/jophiel/user';
import { getRatingClass } from '../../modules/api/jophiel/userRating';
import { Profile } from '../../modules/api/jophiel/profile';
import { AppState } from '../../modules/store';
import { selectUserProfile, selectIsUserWebConfigLoaded } from '../../routes/jophiel/modules/userWebSelectors';
import * as avatarActions from '../../routes/jophiel/modules/avatarActions';

import './UserWidget.css';

export interface UserWidgetProps {
  user?: User;
  isWebConfigLoaded?: boolean;
  profile?: Profile;
  onRenderAvatar: (userJid?: string) => Promise<string>;
}

interface UserWidgetState {
  avatarUrl?: string;
}

export class UserWidget extends React.PureComponent<UserWidgetProps, UserWidgetState> {
  state: UserWidgetState = {};

  componentDidMount() {
    this.refreshUser();
  }

  async componentDidUpdate(prevProps: UserWidgetProps) {
    if (this.props.user !== prevProps.user) {
      this.refreshUser();
    }
  }

  render() {
    if (!this.props.isWebConfigLoaded) {
      return null;
    }
    if (this.props.profile) {
      return this.renderForUser(this.state.avatarUrl!, this.props.profile);
    } else {
      return this.renderForGuest();
    }
  }

  private refreshUser = async () => {
    const { user, onRenderAvatar } = this.props;
    if (user) {
      const avatarUrl = await onRenderAvatar(user.jid);
      this.setState({ avatarUrl });
    }
  };

  private renderForUser = (avatarUrl: string, profile: Profile) => {
    const menu = (
      <Menu className="widget-user__menu">
        <MenuItem className="widget-user__menu-helper" icon="user" text={profile.username} disabled />
        <MenuDivider className="widget-user__menu-helper" />
        <MenuItemLink text="My profile" to={`/profiles/${profile.username}`} />
        {APP_CONFIG.mode !== Mode.PRIVATE_CONTESTS && <MenuItemLink text="My account" to="/account" />}
        <MenuItemLink text="Log out" to="/logout" />
      </Menu>
    );

    const popover = (
      <Popover className="widget-user__avatar-menu" content={menu} position={Position.BOTTOM_RIGHT} usePortal={false}>
        <div className="widget-user__profile">
          <span className="widget-user__user__username">
            <span data-key="username" className={getRatingClass(profile.rating)}>
              {profile.username}
            </span>
          </span>{' '}
          <Icon icon="chevron-down" color="#252627" />
        </div>
      </Popover>
    );

    const responsivePopover = (
      <Popover className="widget-user__burger" content={menu} position={Position.BOTTOM_RIGHT} usePortal={false}>
        <Icon icon="menu" iconSize={Icon.SIZE_LARGE} />
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

  private renderForGuest = () => {
    return (
      <Navbar.Group align={Alignment.RIGHT}>
        <div className="widget-user__link">
          <Link data-key="login" to="/login">
            Log in
          </Link>
        </div>
        {APP_CONFIG.mode !== Mode.PRIVATE_CONTESTS && (
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

  private renderGuestResponsiveMenu = () => {
    const menu = (
      <Menu className="widget-user__menu">
        <MenuItemLink text="Log in" to="/login" />
        {APP_CONFIG.mode !== Mode.PRIVATE_CONTESTS && <MenuItemLink text="Register" to="/register" />}
      </Menu>
    );

    return (
      <Popover className="widget-user__burger" content={menu} position={Position.BOTTOM_RIGHT} usePortal={false}>
        <Icon icon="menu" iconSize={Icon.SIZE_LARGE} />
      </Popover>
    );
  };
}

const mapStateToProps = (state: AppState) => ({
  user: state.session.user,
  isWebConfigLoaded: selectIsUserWebConfigLoaded(state),
  profile: selectUserProfile(state),
});

const mapDispatchToProps = {
  onRenderAvatar: avatarActions.renderAvatar,
};

export default connect(mapStateToProps, mapDispatchToProps)(UserWidget);
