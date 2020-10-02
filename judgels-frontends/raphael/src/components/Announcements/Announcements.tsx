import { Icon, Navbar } from '@blueprintjs/core';
import * as React from 'react';

import { APP_CONFIG } from '../../conf';

import './Announcements.css';

class Announcements extends React.PureComponent {
  render() {
    const announcements = APP_CONFIG.announcements || '';
    if (!announcements) {
      return null;
    }
    return announcements.split(';').map(announcemet => (
      <Navbar className="announcement">
        <div className="announcement__wrapper">
          <Navbar.Heading className="announcement__text">
            <Icon icon="warning-sign" />
            &nbsp;&nbsp;
            {announcemet}
          </Navbar.Heading>
        </div>
      </Navbar>
    ));
  }
}

export default Announcements;
