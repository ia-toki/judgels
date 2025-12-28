import { useEffect } from 'react';
import { useDispatch } from 'react-redux';
import { useParams, useResolvedPath } from 'react-router-dom';

import * as breadcrumbsActions from '../../../../modules/breadcrumbs/breadcrumbsActions';
import * as profileActions from '../../modules/profileActions';

export default function SingleProfileDataRoute() {
  const { username } = useParams();
  const { pathname } = useResolvedPath('');
  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(profileActions.getUser(username));
    dispatch(breadcrumbsActions.pushBreadcrumb(pathname, username));

    return () => {
      dispatch(profileActions.clearUser());
      dispatch(breadcrumbsActions.popBreadcrumb(pathname));
    };
  }, []);

  return null;
}
