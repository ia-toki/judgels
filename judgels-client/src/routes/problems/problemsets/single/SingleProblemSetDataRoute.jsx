import { useEffect } from 'react';
import { useDispatch } from 'react-redux';
import { useParams, useRouteMatch } from 'react-router-dom';

import * as breadcrumbsActions from '../../../../modules/breadcrumbs/breadcrumbsActions';
import * as problemSetActions from '../modules/problemSetActions';

export default function SingleProblemSetDataRoute() {
  const { problemSetSlug } = useParams();
  const match = useRouteMatch();
  const dispatch = useDispatch();

  const loadProblemSet = async () => {
    const problemSet = await dispatch(problemSetActions.getProblemSetBySlug(problemSetSlug));
    dispatch(breadcrumbsActions.pushBreadcrumb(match.url, problemSet.name));
  };

  useEffect(() => {
    loadProblemSet();

    return () => {
      dispatch(problemSetActions.clearProblemSet());
      dispatch(breadcrumbsActions.popBreadcrumb(match.url));
    };
  }, []);

  return null;
}
