import { Button, ButtonGroup } from '@blueprintjs/core';
import { useLocation, useNavigate } from 'react-router-dom';

import './ItemSubmissionUserFilter.scss';

export default function ItemSubmissionUserFilter() {
  const location = useLocation();
  const navigate = useNavigate();

  const isAll = () => {
    return (location.pathname + '/').includes('/all/');
  };

  const clickMine = () => {
    if (isAll()) {
      const idx = location.pathname.lastIndexOf('/all');
      navigate(location.pathname.substr(0, idx));
    }
  };

  const clickAll = () => {
    if (!isAll()) {
      navigate((location.pathname + '/all').replace('//', '/'));
    }
  };

  return (
    <ButtonGroup className="submission-user-filter" fill>
      <Button active={!isAll()} text="My result" onClick={clickMine} />
      <Button active={isAll()} text="All submissions" onClick={clickAll} />
    </ButtonGroup>
  );
}
