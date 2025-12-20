import { useEffect } from 'react';
import { useDispatch } from 'react-redux';
import { useParams, useRouteMatch } from 'react-router-dom';

import * as breadcrumbsActions from '../../../../modules/breadcrumbs/breadcrumbsActions';
import * as profileActions from '../../modules/profileActions';

export default function SingleProfileDataRoute() {
  const { username } = useParams();
  const match = useRouteMatch();
  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(profileActions.getUser(username));
    dispatch(breadcrumbsActions.pushBreadcrumb(match.url, username));

    return () => {
      dispatch(profileActions.clearUser());
      dispatch(breadcrumbsActions.popBreadcrumb(match.url));
    };
  }, []);

  return null;
}
