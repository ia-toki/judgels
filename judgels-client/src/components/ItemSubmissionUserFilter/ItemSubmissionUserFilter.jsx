import { ButtonGroup } from '@blueprintjs/core';
import { useLocation } from '@tanstack/react-router';

import { ButtonLink } from '../ButtonLink/ButtonLink';

import './ItemSubmissionUserFilter.scss';

export default function ItemSubmissionUserFilter() {
  const location = useLocation();

  const isAll = (location.pathname + '/').includes('/all/');

  const basePath = isAll ? location.pathname.substring(0, location.pathname.lastIndexOf('/all')) : location.pathname;
  const allPath = (basePath + '/all').replace('//', '/');

  return (
    <ButtonGroup className="submission-user-filter" fill>
      <ButtonLink to={basePath} active={!isAll}>
        My result
      </ButtonLink>
      <ButtonLink to={allPath} active={isAll}>
        All submissions
      </ButtonLink>
    </ButtonGroup>
  );
}
