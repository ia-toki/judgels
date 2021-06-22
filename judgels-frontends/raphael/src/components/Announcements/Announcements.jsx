import { Navbar } from '@blueprintjs/core';
import { WarningSign } from '@blueprintjs/icons';
import { connect } from 'react-redux';

import './Announcements.scss';

function Announcements({ announcements }) {
  if (!announcements) {
    return null;
  }
  return announcements.map(announcemet => (
    <Navbar className="announcement">
      <div className="announcement__wrapper">
        <Navbar.Heading className="announcement__text">
          <WarningSign />
          &nbsp;&nbsp;
          {announcemet}
        </Navbar.Heading>
      </div>
    </Navbar>
  ));
}

const mapStateToProps = state => ({
  announcements: state.jophiel.userWeb.config.announcements,
});

export default connect(mapStateToProps)(Announcements);
