import { Alignment, Navbar } from '@blueprintjs/core';
import * as React from 'react';
import { Link } from 'react-router-dom';

import { APP_CONFIG } from '../../conf';
import UserWidget from '../../components/UserWidget/UserWidget';

import './Header.css';

const logo = require('../../assets/images/logo-header.png');

export interface HeaderProps {
  userWidget: React.ComponentType<any>;
}

class Header extends React.PureComponent<HeaderProps> {
  render() {
    const UW = this.props.userWidget;

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

          {<UW />}
        </div>
      </Navbar>
    );
  }
}

export default () => <Header userWidget={UserWidget} />;
