import { Button, ButtonGroup } from '@blueprintjs/core';
import { useLocation, useNavigate } from 'react-router-dom';

import './SubmissionUserFilter.scss';

export default function SubmissionUserFilter() {
  const location = useLocation();
  const navigate = useNavigate();

  const isMine = () => {
    return (location.pathname + '/').includes('/mine/');
  };

  const clickAll = () => {
    if (isMine()) {
      const idx = location.pathname.lastIndexOf('/mine');
      navigate(location.pathname.substr(0, idx));
    }
  };

  const clickMine = () => {
    if (!isMine()) {
      navigate((location.pathname + '/mine').replace('//', '/'));
    }
  };

  return (
    <ButtonGroup className="submission-user-filter" fill>
      <Button active={!isMine()} text="All submissions" onClick={clickAll} />
      <Button active={isMine()} text="My submissions" onClick={clickMine} />
    </ButtonGroup>
  );
}
