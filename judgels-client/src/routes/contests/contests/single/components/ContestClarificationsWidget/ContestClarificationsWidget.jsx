import { Intent, Tag } from '@blueprintjs/core';
import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect, useRef } from 'react';

import { ContestClarificationStatus } from '../../../../../../modules/api/uriel/contestClarification';
import { REFRESH_WEB_CONFIG_INTERVAL } from '../../../../../../modules/api/uriel/contestWeb';
import { showDesktopNotification } from '../../../../../../modules/notification/notification';
import { contestBySlugQueryOptions } from '../../../../../../modules/queries/contest';
import { contestWebConfigQueryOptions } from '../../../../../../modules/queries/contestWeb';

import * as toastActions from '../../../../../../modules/toast/toastActions';

export default function ContestClarificationsWidget() {
  const { contestSlug } = useParams({ strict: false });
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));
  const { data: webConfig } = useSuspenseQuery(contestWebConfigQueryOptions(contest.jid));
  const clarificationCount = webConfig.clarificationCount;
  const clarificationStatus = webConfig.clarificationStatus;
  const prevClarificationCountRef = useRef(clarificationCount);

  useEffect(() => {
    if (clarificationCount > prevClarificationCountRef.current) {
      // TODO(lungsin): change the notification tag to be more proper, e.g. using clarification JID.
      const timestamp = Math.floor(Date.now() / REFRESH_WEB_CONFIG_INTERVAL);
      const notificationTag = `clarification_${contestSlug}_timestamp_${timestamp}`;

      let title, message;
      if (clarificationStatus === ContestClarificationStatus.Answered) {
        title = 'New answered clarification(s)';
        message = 'You have new answered clarification(s).';
      } else {
        title = 'New clarification(s)';
        message = 'You have new clarification(s).';
      }
      toastActions.showAlertToast(message);
      showDesktopNotification(title, notificationTag, message);
    }
    prevClarificationCountRef.current = clarificationCount;
  }, [clarificationCount]);

  if (clarificationCount === 0) {
    return null;
  }

  const intent = clarificationStatus === ContestClarificationStatus.Asked ? Intent.WARNING : Intent.NONE;

  return (
    <Tag className="normal-weight" intent={intent}>
      {clarificationCount}
    </Tag>
  );
}
