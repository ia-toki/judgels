import { Callout } from '@blueprintjs/core';
import * as HTMLReactParser from 'html-react-parser';
import * as React from 'react';
import { FormattedRelative } from 'react-intl';

import { ContestAnnouncement } from '../../../../../../../../../../modules/api/uriel/contestAnnouncement';

import './ContestAnnouncementCard.css';

export interface ContestAnnouncementCardProps {
  announcement: ContestAnnouncement;
}

export const ContestAnnouncementCard = (props: ContestAnnouncementCardProps) => (
  <Callout className="contest-announcement-card" title={props.announcement.title}>
    <p className="contest-announcement-card__info">
      <small>
        <FormattedRelative value={props.announcement.updatedTime} />
      </small>
    </p>
    <div className="clearfix" />
    <hr />
    {HTMLReactParser(props.announcement.content)}
  </Callout>
);
