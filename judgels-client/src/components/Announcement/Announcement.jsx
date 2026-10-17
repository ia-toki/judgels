import { Navbar } from '@blueprintjs/core';
import { WarningSign } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useSuspenseQuery } from '@tanstack/react-query';
import HTMLReactParser from 'html-react-parser';

import { userWebConfigQueryOptions } from '../../modules/queries/userWeb';

import './Announcement.scss';

export default function Announcement() {
  const { data } = useSuspenseQuery(userWebConfigQueryOptions());
  const announcement = data.appAnnouncement;

  if (!announcement) {
    return null;
  }
  return (
    <Navbar className="announcement">
      <Flex asChild alignItems="center" justifyContent="center" gap={2}>
        <Navbar.Heading className="announcement__text">
          <WarningSign className="announcement__icon" />
          <div className="announcement__body">{HTMLReactParser(announcement)}</div>
        </Navbar.Heading>
      </Flex>
    </Navbar>
  );
}
