import { Button, ButtonGroup } from '@blueprintjs/core';
import { useHistory, useLocation } from 'react-router-dom';

import './ItemSubmissionUserFilter.scss';

export default function ItemSubmissionUserFilter() {
  const location = useLocation();
  const history = useHistory();

  const isAll = () => {
    return (location.pathname + '/').includes('/all/');
  };

  const clickMine = () => {
    if (isAll()) {
      const idx = location.pathname.lastIndexOf('/all');
      history.push(location.pathname.substr(0, idx));
    }
  };

  const clickAll = () => {
    if (!isAll()) {
      history.push((location.pathname + '/all').replace('//', '/'));
    }
  };

  return (
    <ButtonGroup className="submission-user-filter" fill>
      <Button active={!isAll()} text="My result" onClick={clickMine} />
      <Button active={isAll()} text="All submissions" onClick={clickAll} />
    </ButtonGroup>
  );
}
