import { Alignment, Menu, MenuDivider, MenuItem, Navbar, Popover, Position } from '@blueprintjs/core';
import { ChevronDown, Menu as IconMenu } from '@blueprintjs/icons';
import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { Link, useNavigate } from '@tanstack/react-router';

import { isTLX } from '../../conf';
import { getRatingClass } from '../../modules/api/userRating';
import { logOutMutationOptions } from '../../modules/queries/session';
import { avatarUrlQueryOptions } from '../../modules/queries/userAvatar';
import { userWebConfigQueryOptions } from '../../modules/queries/userWeb';
import { useSession } from '../../modules/session';
import MenuItemLink from '../MenuItemLink/MenuItemLink';

import './UserWidget.scss';

export function UserWidget({ user, profile, items, homeRoute }) {
  const navigate = useNavigate();
  const { data: avatarUrl } = useQuery({
    ...avatarUrlQueryOptions(user?.jid),
    enabled: !!user,
  });
  const logOutMutation = useMutation(logOutMutationOptions);

  const logOut = () => {
    logOutMutation.mutate(undefined, { onSuccess: () => navigate({ to: '/' }) });
  };

  const renderForUser = () => {
    const menuItems = (
      <>
        <MenuItemLink text="My profile" to={`/profiles/${profile.username}`} />
        {isTLX() && <MenuItemLink text="My account" to="/account" />}
        <MenuItem text="Log out" onClick={logOut} />
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
        <MenuItemLink text={homeRoute.title} to="/" />
        {items.map(item => (
          <MenuItemLink key={item.route.path} text={item.title} to={item.route.path} />
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

  const renderForGuest = () => {
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
        {renderGuestResponsiveMenu()}
      </Navbar.Group>
    );
  };

  const renderGuestResponsiveMenu = () => {
    const menu = (
      <Menu className="widget-user__menu">
        <MenuItemLink text={homeRoute.title} to="/" />
        {items.map(item => (
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
