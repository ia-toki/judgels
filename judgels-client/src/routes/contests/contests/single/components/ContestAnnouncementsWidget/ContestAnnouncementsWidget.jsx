import { Tag } from '@blueprintjs/core';
import { useParams } from '@tanstack/react-router';
import { useEffect, useRef } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { REFRESH_WEB_CONFIG_INTERVAL } from '../../../../../../modules/api/uriel/contestWeb';
import { selectContestWebConfig } from '../../../modules/contestWebConfigSelectors';

import * as contestAnnouncementActions from '../../announcements/modules/contestAnnouncementActions';

export default function ContestAnnouncementsWidget() {
  const { contestSlug } = useParams({ strict: false });
  const dispatch = useDispatch();
  const announcementCount = useSelector(state => selectContestWebConfig(state).announcementCount);
  const prevAnnouncementCountRef = useRef(announcementCount);

  useEffect(() => {
    if (announcementCount > prevAnnouncementCountRef.current) {
      // TODO(lungsin): change the notification tag to be more proper, e.g. using announcement JID.
      const timestamp = Math.floor(Date.now() / REFRESH_WEB_CONFIG_INTERVAL); // Use timestamp for notification tag
      const notificationTag = `announcement_${contestSlug}_timestamp_${timestamp}`;
      dispatch(contestAnnouncementActions.alertNewAnnouncements(notificationTag));
    }
    prevAnnouncementCountRef.current = announcementCount;
  }, [announcementCount]);

  if (announcementCount === 0) {
    return null;
  }

  return <Tag className="normal-weight">{announcementCount}</Tag>;
}
