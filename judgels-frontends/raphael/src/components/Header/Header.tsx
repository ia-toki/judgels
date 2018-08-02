import * as React from 'react';
import { Link } from 'react-router-dom';

import { APP_CONFIG } from 'conf';
import UserWidget from 'components/UserWidget/UserWidget';

import './Header.css';

const logo = require('assets/images/logo.png');

export interface HeaderProps {
  userWidget: React.ComponentType<any>;
}

class Header extends React.PureComponent<HeaderProps> {
  render() {
    const UW = this.props.userWidget;

    return (
      <nav className="bp3-navbar header">
        <div className="header__wrapper">
          <div className="bp3-navbar-group bp3-align-left">
            <div>
              <Link to="/">
                <img src={logo} className="header__logo" />
              </Link>
            </div>
            <div className="bp3-navbar-heading header__title">{APP_CONFIG.name}</div>
            <span className="bp3-navbar-divider" />
            <div className="header__subtitle">{APP_CONFIG.slogan}</div>
          </div>

          {<UW />}
        </div>
      </nav>
    );
  }
}

export default () => <Header userWidget={UserWidget} />;
