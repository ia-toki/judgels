import { Alignment, Navbar } from '@blueprintjs/core';
import { Link } from 'react-router';

import logo from '../../assets/images/logo-header.png';
import { APP_CONFIG } from '../../conf';
import DarkModeWidget from '../DarkModeWidget/DarkModeWidget';
import Menubar from '../Menubar/Menubar';
import UserWidget from '../UserWidget/UserWidget';

import './Header.scss';

export default function Header({ items, homeRoute }) {
  return (
    <Navbar className="header">
      <div className="header__wrapper">
        <Navbar.Group align={Alignment.LEFT}>
          <div>
            <Link to="/">
              <img src={logo} alt="header" className="header__logo" />
            </Link>
          </div>
          <Navbar.Heading className="header__title">{APP_CONFIG.name}</Navbar.Heading>
          <Navbar.Divider className="header__subtitle-wrapper" />
          <div className="header__subtitle header__subtitle-wrapper">{APP_CONFIG.slogan}</div>
          <Navbar.Divider />

          <Menubar items={items} homeRoute={homeRoute} />
        </Navbar.Group>

        <UserWidget items={items} homeRoute={homeRoute} />
        <DarkModeWidget />
      </div>
    </Navbar>
  );
}
