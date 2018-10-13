import { Callout, Tag } from '@blueprintjs/core';
import * as React from 'react';
import { FormattedRelative } from 'react-intl';

import { FormattedContent } from 'components/FormattedContent/FormattedContent';
import {
  ContestAnnouncement,
  ContestAnnouncementData,
  ContestAnnouncementStatus,
} from 'modules/api/uriel/contestAnnouncement';
import { Contest } from 'modules/api/uriel/contest';

import { ContestAnnouncementEditDialog } from '../ContestAnnouncementEditDialog/ContestAnnouncementEditDialog';

import './ContestAnnouncementCard.css';

export interface ContestAnnouncementCardProps {
  announcement: ContestAnnouncement;
  isAllowedToEditAnnouncement: boolean;
  contest: Contest;
  onRefreshAnnouncements: () => Promise<void>;
  onUpdateAnnouncement: (contestJid: string, announcementJid: string, data: ContestAnnouncementData) => void;
}

export const ContestAnnouncementCard = (props: ContestAnnouncementCardProps) => (
  <Callout className="contest-announcement-card" title={props.announcement.title}>
    {props.announcement.status === ContestAnnouncementStatus.Draft ? <Tag>Draft</Tag> : null}
    <p className="contest-announcement-card__info">
      <small>
        published <FormattedRelative value={props.announcement.updatedTime} />{' '}
        <ContestAnnouncementEditDialog {...props} />
      </small>
    </p>
    <div className="clearfix" />
    <hr />
    <FormattedContent context={{ contestJid: props.contest.jid }}>{props.announcement.content}</FormattedContent>
  </Callout>
);
