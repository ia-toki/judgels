import { Intent, Tag } from '@blueprintjs/core';
import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect, useRef } from 'react';

import { ContestClarificationStatus } from '../../../../../../modules/api/uriel/contestClarification';
import { REFRESH_WEB_CONFIG_INTERVAL } from '../../../../../../modules/api/uriel/contestWeb';
import { callAction } from '../../../../../../modules/callAction';
import { contestWebConfigQueryOptions } from '../../../../../../modules/queries/contestWeb';
import { useSession } from '../../../../../../modules/session';

import * as contestClarificationActions from '../../clarifications/modules/contestClarificationActions';

export default function ContestClarificationsWidget() {
  const { contestSlug } = useParams({ strict: false });
  const { token } = useSession();
  const { data: webConfig } = useSuspenseQuery(contestWebConfigQueryOptions(token, contestSlug));
  const clarificationCount = webConfig.clarificationCount;
  const clarificationStatus = webConfig.clarificationStatus;
  const prevClarificationCountRef = useRef(clarificationCount);

  useEffect(() => {
    if (clarificationCount > prevClarificationCountRef.current) {
      // TODO(lungsin): change the notification tag to be more proper, e.g. using clarification JID.
      const timestamp = Math.floor(Date.now() / REFRESH_WEB_CONFIG_INTERVAL);
      const notificationTag = `clarification_${contestSlug}_timestamp_${timestamp}`;
      callAction(contestClarificationActions.alertNewClarifications(clarificationStatus, notificationTag));
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
