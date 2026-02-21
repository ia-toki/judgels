import { Tag } from '@blueprintjs/core';
import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect, useRef } from 'react';

import { REFRESH_WEB_CONFIG_INTERVAL } from '../../../../../../modules/api/uriel/contestWeb';
import { showDesktopNotification } from '../../../../../../modules/notification/notification';
import { contestWebConfigQueryOptions } from '../../../../../../modules/queries/contestWeb';

import * as toastActions from '../../../../../../modules/toast/toastActions';

export default function ContestAnnouncementsWidget() {
  const { contestSlug } = useParams({ strict: false });
  const { data: webConfig } = useSuspenseQuery(contestWebConfigQueryOptions(contestSlug));
  const announcementCount = webConfig.announcementCount;
  const prevAnnouncementCountRef = useRef(announcementCount);

  useEffect(() => {
    if (announcementCount > prevAnnouncementCountRef.current) {
      // TODO(lungsin): change the notification tag to be more proper, e.g. using announcement JID.
      const timestamp = Math.floor(Date.now() / REFRESH_WEB_CONFIG_INTERVAL); // Use timestamp for notification tag
      const notificationTag = `announcement_${contestSlug}_timestamp_${timestamp}`;
      const message = 'You have new announcement(s).';
      toastActions.showAlertToast(message);
      showDesktopNotification('New announcement(s)', notificationTag, message);
    }
    prevAnnouncementCountRef.current = announcementCount;
  }, [announcementCount]);

  if (announcementCount === 0) {
    return null;
  }

  return <Tag className="normal-weight">{announcementCount}</Tag>;
}
