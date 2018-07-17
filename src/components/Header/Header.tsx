import * as React from 'react';
import { Link } from 'react-router-dom';

import { APP_CONFIG } from '../../conf';
import UserWidgetContainer from '../UserWidget/UserWidget';

import './Header.css';
const logo = require('../../assets/images/logo.png');

export interface HeaderProps {
  userWidget: React.ComponentType<any>;
}

export const Header = (props: HeaderProps) => {
  const UW = props.userWidget;

  return (
    <nav className="pt-navbar header">
      <div className="header__wrapper">
        <div className="pt-navbar-group pt-align-left">
          <div>
            <Link to="/">
              <img src={logo} className="header__logo" />
            </Link>
          </div>
          <div className="pt-navbar-heading header__title">{APP_CONFIG.name}</div>
          <span className="pt-navbar-divider" />
          <div className="header__subtitle">{APP_CONFIG.slogan}</div>
        </div>

        {<UW />}
      </div>
    </nav>
  );
};

const HeaderContainer = () => <Header userWidget={UserWidgetContainer} />;
export default HeaderContainer;
