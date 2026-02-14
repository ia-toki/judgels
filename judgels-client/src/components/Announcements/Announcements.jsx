import { Navbar } from '@blueprintjs/core';
import { WarningSign } from '@blueprintjs/icons';
import { useSuspenseQuery } from '@tanstack/react-query';
import HTMLReactParser from 'html-react-parser';
import { useSelector } from 'react-redux';

import { userWebConfigQueryOptions } from '../../modules/queries/userWeb';
import { selectToken } from '../../modules/session/sessionSelectors';

import './Announcements.scss';

export default function Announcements() {
  const token = useSelector(selectToken);
  const { data } = useSuspenseQuery(userWebConfigQueryOptions(token));
  const announcements = data.announcements;

  if (!announcements) {
    return null;
  }
  return announcements.map(announcemet => (
    <Navbar className="announcement">
      <div className="announcement__wrapper">
        <Navbar.Heading className="announcement__text">
          <WarningSign />
          &nbsp;&nbsp;
          {HTMLReactParser(announcemet)}
        </Navbar.Heading>
      </div>
    </Navbar>
  ));
}
