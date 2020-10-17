import { Icon, Navbar } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';

import { AppState } from '../../modules/store';
import { WebConfig } from '../../modules/api/jophiel/web';

import './Announcements.css';

interface AnnouncementsProps {
  config: WebConfig;
}

class Announcements extends React.PureComponent<AnnouncementsProps> {
  render() {
    const announcements = this.props.config.announcements || [];
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
}

const mapStateToProps = (state: AppState) => ({
  config: state.jophiel.web.config,
});

export default connect(mapStateToProps)(Announcements);
