import { Navbar } from '@blueprintjs/core';
import { WarningSign } from '@blueprintjs/icons';
import { useSuspenseQuery } from '@tanstack/react-query';
import HTMLReactParser from 'html-react-parser';

import { userWebConfigQueryOptions } from '../../modules/queries/userWeb';
import { useSession } from '../../modules/session';

import './Announcements.scss';

export default function Announcements() {
  const { token } = useSession();
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
