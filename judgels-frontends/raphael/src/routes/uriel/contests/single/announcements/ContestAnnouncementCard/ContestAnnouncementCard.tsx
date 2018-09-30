import { Callout, Tag } from '@blueprintjs/core';
import * as React from 'react';
import { FormattedRelative } from 'react-intl';

import { HtmlText } from 'components/HtmlText/HtmlText';
import { ContestAnnouncement, ContestAnnouncementStatus } from 'modules/api/uriel/contestAnnouncement';

import './ContestAnnouncementCard.css';

export interface ContestAnnouncementCardProps {
  announcement: ContestAnnouncement;
}

export const ContestAnnouncementCard = (props: ContestAnnouncementCardProps) => (
  <Callout className="contest-announcement-card" title={props.announcement.title}>
    {props.announcement.status === ContestAnnouncementStatus.Draft ? <Tag>Draft</Tag> : null}
    <p className="contest-announcement-card__info">
      <small>
        published <FormattedRelative value={props.announcement.updatedTime} />
      </small>
    </p>
    <div className="clearfix" />
    <hr />
    <HtmlText>{props.announcement.content}</HtmlText>
  </Callout>
);
