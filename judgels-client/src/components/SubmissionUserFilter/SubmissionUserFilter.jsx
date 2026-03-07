import { ButtonGroup } from '@blueprintjs/core';
import { useLocation } from '@tanstack/react-router';

import { ButtonLink } from '../ButtonLink/ButtonLink';

import './SubmissionUserFilter.scss';

export default function SubmissionUserFilter() {
  const location = useLocation();

  const isMine = (location.pathname + '/').includes('/mine/');

  const basePath = isMine ? location.pathname.substring(0, location.pathname.lastIndexOf('/mine')) : location.pathname;
  const minePath = (basePath + '/mine').replace('//', '/');

  return (
    <ButtonGroup className="submission-user-filter" fill>
      <ButtonLink to={basePath} active={!isMine}>
        All submissions
      </ButtonLink>
      <ButtonLink to={minePath} active={isMine}>
        My submissions
      </ButtonLink>
    </ButtonGroup>
  );
}
