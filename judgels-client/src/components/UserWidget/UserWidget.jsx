import { Alignment, Menu, MenuDivider, MenuItem, Navbar, Popover, Position } from '@blueprintjs/core';
import { ChevronDown, Menu as IconMenu } from '@blueprintjs/icons';
import { Box } from '@blueprintjs/labs';
import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { Link, useNavigate } from '@tanstack/react-router';

import { getRatingClass } from '../../modules/api/userRating';
import { logOutMutationOptions } from '../../modules/queries/session';
import { userAvatarUrlQueryOptions } from '../../modules/queries/userAvatar';
import { userWebConfigQueryOptions } from '../../modules/queries/userWeb';
import { useSession } from '../../modules/session';
import { isUserRegistrationEnabled, isUserResetPasswordEnabled } from '../../modules/webConfig';
import MenuItemLink from '../MenuItemLink/MenuItemLink';

import './UserWidget.scss';

export function UserWidget({ user, profile, items, homeRoute }) {
  const navigate = useNavigate();
  const { data: avatarUrl } = useQuery({
    ...userAvatarUrlQueryOptions(user?.jid),
    enabled: !!user,
  });
  const logOutMutation = useMutation(logOutMutationOptions);

  const logOut = () => {
    logOutMutation.mutate(undefined, { onSuccess: () => navigate({ to: '/', ignoreBlocker: true }) });
  };

  const renderForUser = () => {
    const menuItems = (
      <>
        <MenuItemLink text="My profile" to={`/profiles/${profile.username}`} />
        {(isUserRegistrationEnabled() || isUserResetPasswordEnabled()) && (
          <MenuItemLink text="My account" to="/account" />
        )}
        <MenuItem text="Log out" onClick={logOut} />
      </>
    );

    const menu = <Menu className="user-widget__menu">{menuItems}</Menu>;

    const popover = (
      <Popover className="user-widget__avatar-menu" content={menu} position={Position.BOTTOM_RIGHT} usePortal={false}>
        <div className="user-widget__profile">
          <span className="user-widget__username">
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
        <MenuItemLink text={homeRoute.title} to="/" />
        {items.map(item => (
          <MenuItemLink key={item.route.path} text={item.title} to={item.route.path} />
        ))}
        <MenuDivider className="user-widget__menu-helper" />
        {menuItems}
      </Menu>
    );

    const responsivePopover = (
      <Popover
        className="user-widget__burger"
        content={responsiveMenu}
        position={Position.BOTTOM_RIGHT}
        usePortal={false}
      >
        <IconMenu />
      </Popover>
    );

    return (
      <Navbar.Group align={Alignment.RIGHT}>
        <div className="user-widget__avatar-wrapper">
          <img src={avatarUrl} alt="avatar" className="user-widget__avatar" />
        </div>
        {popover}
        {responsivePopover}
      </Navbar.Group>
    );
  };

  const renderForGuest = () => {
    return (
      <Navbar.Group align={Alignment.RIGHT}>
        <Box className="user-widget__link" marginRight={5}>
          <Link data-key="login" to="/login">
            Log in
          </Link>
        </Box>
        {isUserRegistrationEnabled() && (
          <Box className="user-widget__link" marginRight={5}>
            <Link data-key="register" to="/register">
              Register
            </Link>
          </Box>
        )}
        {renderGuestResponsiveMenu()}
      </Navbar.Group>
    );
  };

  const renderGuestResponsiveMenu = () => {
    const menu = (
      <Menu className="user-widget__menu">
        <MenuItemLink text={homeRoute.title} to="/" />
        {items.map(item => (
          <MenuItemLink key={item.route.path} text={item.title} to={item.route.path} />
        ))}
      </Menu>
    );

    return (
      <Popover className="user-widget__burger" content={menu} position={Position.BOTTOM_RIGHT} usePortal={false}>
        <IconMenu />
      </Popover>
    );
  };

  if (profile) {
    return renderForUser();
  }
  return renderForGuest();
}

function UserWidgetContainer({ items, homeRoute }) {
  const { user } = useSession();
  const { data } = useSuspenseQuery(userWebConfigQueryOptions());

  return <UserWidget user={user} profile={data.profile} items={items} homeRoute={homeRoute} />;
}

export default UserWidgetContainer;
