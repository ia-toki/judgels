import { ButtonGroup, Button } from '@blueprintjs/core';
import { push } from 'connected-react-router';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import './ItemSubmissionUserFilter.css';

function ItemSubmissionUserFilter({ location, push }) {
  const isAll = () => {
    return (location.pathname + '/').includes('/all/');
  };

  const clickMine = () => {
    if (isAll()) {
      const idx = location.pathname.lastIndexOf('/all');
      push(location.pathname.substr(0, idx));
    }
  };

  const clickAll = () => {
    if (!isAll()) {
      push((location.pathname + '/all').replace('//', '/'));
    }
  };

  return (
    <ButtonGroup className="submission-user-filter" fill>
      <Button active={!isAll()} text="My result" onClick={clickMine} />
      <Button active={isAll()} text="All submissions" onClick={clickAll} />
    </ButtonGroup>
  );
}

const mapDispatchToProps = {
  push,
};

export default withRouter(connect(undefined, mapDispatchToProps)(ItemSubmissionUserFilter));
