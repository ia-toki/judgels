import { Button, ButtonGroup } from '@blueprintjs/core';
import { useHistory, useLocation } from 'react-router-dom';

import './SubmissionUserFilter.scss';

export default function SubmissionUserFilter() {
  const location = useLocation();
  const history = useHistory();

  const isMine = () => {
    return (location.pathname + '/').includes('/mine/');
  };

  const clickAll = () => {
    if (isMine()) {
      const idx = location.pathname.lastIndexOf('/mine');
      history.push(location.pathname.substr(0, idx));
    }
  };

  const clickMine = () => {
    if (!isMine()) {
      history.push((location.pathname + '/mine').replace('//', '/'));
    }
  };

  return (
    <ButtonGroup className="submission-user-filter" fill>
      <Button active={!isMine()} text="All submissions" onClick={clickAll} />
      <Button active={isMine()} text="My submissions" onClick={clickMine} />
    </ButtonGroup>
  );
}
