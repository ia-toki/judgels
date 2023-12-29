import { Button, ButtonGroup } from '@blueprintjs/core';
import { push } from 'connected-react-router';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import './SubmissionUserFilter.scss';

function SubmissionUserFilter({ location, push }) {
  const isMine = () => {
    return (location.pathname + '/').includes('/mine/');
  };

  const clickAll = () => {
    if (isMine()) {
      const idx = location.pathname.lastIndexOf('/mine');
      push(location.pathname.substr(0, idx));
    }
  };

  const clickMine = () => {
    if (!isMine()) {
      push((location.pathname + '/mine').replace('//', '/'));
    }
  };

  return (
    <ButtonGroup className="submission-user-filter" fill>
      <Button active={!isMine()} text="All submissions" onClick={clickAll} />
      <Button active={isMine()} text="My submissions" onClick={clickMine} />
    </ButtonGroup>
  );
}

const mapDispatchToProps = {
  push,
};

export default withRouter(connect(undefined, mapDispatchToProps)(SubmissionUserFilter));
