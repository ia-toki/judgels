import { Alignment, Navbar } from '@blueprintjs/core';
import { PureComponent } from 'react';
import { Link } from 'react-router-dom';

import { APP_CONFIG } from '../../conf';
import DarkModeWidget from '../DarkModeWidget/DarkModeWidget';
import UserWidget from '../UserWidget/UserWidget';

import './Header.scss';

import logo from '../../assets/images/logo-header.png';

export default class Header extends PureComponent {
  render() {
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
            <Navbar.Divider />
            <div className="header__subtitle">{APP_CONFIG.slogan}</div>
          </Navbar.Group>

          <UserWidget />
          <DarkModeWidget />
        </div>
      </Navbar>
    );
  }
}
