import { Icon, Navbar } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';

import './Announcements.css';

function Announcements({ config }) {
  const announcements = config.announcements || [];
  if (!announcements) {
    return null;
  }
  return announcements.map(announcemet => (
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

const mapStateToProps = state => ({
  config: state.jophiel.web.config,
});

export default connect(mapStateToProps)(Announcements);
