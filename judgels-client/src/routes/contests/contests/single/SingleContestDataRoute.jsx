import { useEffect, useRef } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useParams, useResolvedPath } from 'react-router-dom';

import { REFRESH_WEB_CONFIG_INTERVAL } from '../../../../modules/api/uriel/contestWeb';
import { selectContest } from '../modules/contestSelectors';

import * as breadcrumbsActions from '../../../../modules/breadcrumbs/breadcrumbsActions';
import * as contestActions from '../modules/contestActions';
import * as contestWebActions from './modules/contestWebActions';

export default function SingleContestDataRoute() {
  const { contestSlug } = useParams();
  const { pathname } = useResolvedPath('');
  const dispatch = useDispatch();
  const contest = useSelector(selectContest);
  const currentTimeoutRef = useRef(null);

  const loadContest = async () => {
    // Optimization:
    // If the current contest slug is equal to the persisted one, then assume the JID is still the same,
    if (contest && contest.slug === contestSlug) {
      currentTimeoutRef.current = setTimeout(() => refreshWebConfig(contest.jid), REFRESH_WEB_CONFIG_INTERVAL);
    }

    // so that we don't have to wait until we get the contest from backend.
    const { contest: newContest } = await dispatch(contestWebActions.getContestBySlugWithWebConfig(contestSlug));
    dispatch(breadcrumbsActions.pushBreadcrumb(pathname, newContest.name));

    if (!contest || contest.slug !== contestSlug) {
      currentTimeoutRef.current = setTimeout(() => refreshWebConfig(newContest.jid), REFRESH_WEB_CONFIG_INTERVAL);
    }
  };

  useEffect(() => {
    loadContest();

    return () => {
      dispatch(contestActions.clearContest());
      dispatch(contestWebActions.clearWebConfig());
      dispatch(breadcrumbsActions.popBreadcrumb(pathname));

      if (currentTimeoutRef.current) {
        clearTimeout(currentTimeoutRef.current);
      }
    };
  }, [contestSlug]);

  const refreshWebConfig = async contestJid => {
    await dispatch(contestWebActions.getWebConfig(contestJid));
    currentTimeoutRef.current = setTimeout(() => refreshWebConfig(contestJid), REFRESH_WEB_CONFIG_INTERVAL);
  };

  return null;
}
