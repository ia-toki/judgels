import { ButtonGroup } from '@blueprintjs/core';
import { useLocation } from '@tanstack/react-router';

import { ButtonLink } from '../ButtonLink/ButtonLink';

import './SubmissionUserFilter.scss';

export default function SubmissionUserFilter({
  segment = 'mine',
  baseLabel = 'All submissions',
  segmentLabel = 'My submissions',
}) {
  const location = useLocation();

  const isInSegment = (location.pathname + '/').includes(`/${segment}/`);

  const basePath = isInSegment
    ? location.pathname.substring(0, location.pathname.lastIndexOf(`/${segment}`))
    : location.pathname;
  const segmentPath = (basePath + `/${segment}`).replace('//', '/');

  return (
    <ButtonGroup className="submission-user-filter" fill>
      <ButtonLink to={basePath} active={!isInSegment}>
        {baseLabel}
      </ButtonLink>
      <ButtonLink to={segmentPath} active={isInSegment}>
        {segmentLabel}
      </ButtonLink>
    </ButtonGroup>
  );
}

export function ItemSubmissionUserFilter() {
  return <SubmissionUserFilter segment="all" baseLabel="My result" segmentLabel="All submissions" />;
}
